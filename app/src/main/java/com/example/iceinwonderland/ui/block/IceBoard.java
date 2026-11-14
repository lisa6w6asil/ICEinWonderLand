package com.example.iceinwonderland.ui.block;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 雪山の氷すべりパズルの盤面ルール(画面に依存しない純粋な Java)。
 * 主人公は岩・壁・ゴール・氷ブロックの手前か、雪のマスの上まで滑る。
 * 氷ブロックは岩・壁・主人公の手前か、雪のマス・ゴールに入るまで滑る。
 */
public final class IceBoard {

    public static final int W = 6, H = 10;
    public static final int ICE = 0, WALL = 1, GOAL = 2, SNOW = 3; // WALL は外枠の壁と中の岩の両方
    /** 0=上 1=右 2=下 3=左 */
    public static final int[][] DIRS = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
    public static final int DOWN = 2;

    public static final int MAX_TRIES = 5000;

    // 保険の盤面(最短5回)。P=スタート(雪) B=氷ブロック R=岩 S=雪 .=氷 W=壁 G=ゴール
    private static final String[] FALLBACK_MAP = {
            "WWWWPW", "S..R..", "..S...", "SR...R", "...S..",
            ".R..B.", "..SS..", "R...SS", "R..R..", "WWGGWW"
    };

    // ===== 盤面そのもの(作ったあとは変えない) =====
    private final int[][] grid;
    public final int startX, startY, blockStartX, blockStartY;
    /** 最短で何回蹴れば解けるか */
    public final int best;
    /** 何個目の盤面で合格したか(保険の盤面なら MAX_TRIES) */
    public final int tries;
    public final boolean fallback;

    // ===== 遊んでいる最中の状態 =====
    private int px, py, dir, bx, by, kicks;

    private IceBoard(int[][] grid, int sx, int sy, int bx0, int by0, int best, int tries, boolean fallback) {
        this.grid = grid;
        this.startX = sx;
        this.startY = sy;
        this.blockStartX = bx0;
        this.blockStartY = by0;
        this.best = best;
        this.tries = tries;
        this.fallback = fallback;
        reset();
    }

    // ---------- 遊ぶ側が使うメソッド ----------

    /** B ボタン:同じ盤面を最初から */
    public void reset() {
        px = startX;
        py = startY;
        dir = DOWN;
        bx = blockStartX;
        by = blockStartY;
        kicks = 0;
    }

    /** 十字キー:その向きを向いて、止まるところまで滑る。滑ったマス数を返す(0なら向きだけ変わる) */
    public int move(int d) {
        dir = d;
        int[] p = slidePlayer(grid, px, py, d, bx, by);
        int dist = Math.abs(p[0] - px) + Math.abs(p[1] - py);
        px = p[0];
        py = p[1];
        return dist;
    }

    /** A ボタン:目の前の氷ブロックを蹴る。滑ったマス数を返す(-1=目の前にない、0=その向きには滑らない) */
    public int kick() {
        if (px + DIRS[dir][0] != bx || py + DIRS[dir][1] != by) return -1;
        int[] end = slideBlock(grid, bx, by, dir, px, py);
        if (end == null) return 0;
        int dist = Math.abs(end[0] - bx) + Math.abs(end[1] - by);
        bx = end[0];
        by = end[1];
        kicks++;
        return dist;
    }

    public boolean isCleared() {
        return grid[by][bx] == GOAL;
    }

    public int cell(int x, int y) { return grid[y][x]; }
    public int playerX() { return px; }
    public int playerY() { return py; }
    public int direction() { return dir; }
    public int blockX() { return bx; }
    public int blockY() { return by; }
    public int kicks() { return kicks; }

    // ---------- 盤面を作る ----------

    /** 最低 minKicks 回蹴らないと解けない盤面を作る。重いので画面のスレッドでは呼ばない */
    public static IceBoard make(int minKicks, Random random) {
        for (int t = 1; t <= MAX_TRIES; t++) {
            Generated g = generate(random);
            int k = solve(g.grid, g.sx, g.sy, g.bx, g.by);
            if (k >= minKicks) return new IceBoard(g.grid, g.sx, g.sy, g.bx, g.by, k, t, false);
        }
        return fallbackBoard();
    }

    public static IceBoard fallbackBoard() {
        int[][] grid = new int[H][W];
        int sx = 0, sy = 0, bx0 = 0, by0 = 0;
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                char c = FALLBACK_MAP[y].charAt(x);
                switch (c) {
                    case 'P': sx = x; sy = y; grid[y][x] = SNOW; break;
                    case 'B': bx0 = x; by0 = y; grid[y][x] = ICE; break;
                    case 'W': case 'R': grid[y][x] = WALL; break;
                    case 'G': grid[y][x] = GOAL; break;
                    case 'S': grid[y][x] = SNOW; break;
                    default: grid[y][x] = ICE; break;
                }
            }
        }
        int k = solve(grid, sx, sy, bx0, by0);
        return new IceBoard(grid, sx, sy, bx0, by0, k, MAX_TRIES, true);
    }

    // ---------- ルール本体 ----------

    private static boolean inside(int x, int y) {
        return x >= 0 && y >= 0 && x < W && y < H;
    }

    static int[] slidePlayer(int[][] grid, int px, int py, int d, int bx, int by) {
        int x = px, y = py;
        while (true) {
            int tx = x + DIRS[d][0], ty = y + DIRS[d][1];
            if (!inside(tx, ty)) break;
            int c = grid[ty][tx];
            if (c == WALL || c == GOAL || (tx == bx && ty == by)) break;
            x = tx;
            y = ty;
            if (c == SNOW) break;
        }
        return new int[]{x, y};
    }

    /** 動かなければ null */
    static int[] slideBlock(int[][] grid, int bx, int by, int d, int px, int py) {
        int x = bx, y = by;
        while (true) {
            int tx = x + DIRS[d][0], ty = y + DIRS[d][1];
            if (!inside(tx, ty) || grid[ty][tx] == WALL || (tx == px && ty == py)) break;
            x = tx;
            y = ty;
            if (grid[y][x] == GOAL || grid[y][x] == SNOW) break;
        }
        return (x == bx && y == by) ? null : new int[]{x, y};
    }

    /** 最短で何回蹴れば解けるか(解けなければ -1)。滑る・向きを変えるは0回、蹴るは1回 */
    static int solve(int[][] grid, int sx, int sy, int bx0, int by0) {
        boolean[] seen = new boolean[W * H * 4 * W * H];
        ArrayDeque<int[]> current = new ArrayDeque<>();
        ArrayDeque<int[]> next = new ArrayDeque<>();
        current.push(new int[]{sx, sy, DOWN, bx0, by0});
        int kicks = 0;
        while (!current.isEmpty()) {
            while (!current.isEmpty()) {
                int[] s = current.pop();
                int px = s[0], py = s[1], d = s[2], bx = s[3], by = s[4];
                int id = (((py * W + px) * 4 + d) * H + by) * W + bx;
                if (seen[id]) continue;
                seen[id] = true;
                for (int nd = 0; nd < 4; nd++) {
                    int[] p = slidePlayer(grid, px, py, nd, bx, by);
                    current.push(new int[]{p[0], p[1], nd, bx, by});
                }
                if (px + DIRS[d][0] == bx && py + DIRS[d][1] == by) {
                    int[] end = slideBlock(grid, bx, by, d, px, py);
                    if (end != null) {
                        if (grid[end[1]][end[0]] == GOAL) return kicks + 1;
                        next.push(new int[]{px, py, d, end[0], end[1]});
                    }
                }
            }
            ArrayDeque<int[]> tmp = current;
            current = next;
            next = tmp;
            kicks++;
        }
        return -1;
    }

    private static final class Generated {
        int[][] grid;
        int sx, sy, bx, by;
    }

    private static Generated generate(Random r) {
        Generated g = new Generated();
        g.grid = new int[H][W]; // 0 = ICE
        g.sx = 1 + r.nextInt(W - 2);
        g.sy = 0;
        for (int x = 0; x < W; x++) {
            g.grid[0][x] = WALL;
            g.grid[H - 1][x] = WALL;
        }
        g.grid[0][g.sx] = SNOW;
        g.grid[H - 1][2] = GOAL;
        g.grid[H - 1][3] = GOAL;

        List<int[]> cells = new ArrayList<>();
        for (int y = 1; y < H - 1; y++) for (int x = 0; x < W; x++) cells.add(new int[]{x, y});
        Collections.shuffle(cells, r);

        int rocks = 6 + r.nextInt(5); // 6〜10個
        int snows = 4 + r.nextInt(5); // 4〜8個
        int i = 0;
        for (; i < rocks; i++) g.grid[cells.get(i)[1]][cells.get(i)[0]] = WALL;
        for (int j = 0; j < snows; j++, i++) g.grid[cells.get(i)[1]][cells.get(i)[0]] = SNOW;

        List<int[]> spots = new ArrayList<>();
        for (int k = i; k < cells.size(); k++) {
            int y = cells.get(k)[1];
            if (y >= 2 && y <= H - 3) spots.add(cells.get(k));
        }
        int[] b = spots.get(r.nextInt(spots.size()));
        g.bx = b[0];
        g.by = b[1];
        return g;
    }
}