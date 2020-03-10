package org.firas.tool.ssa;

/**
 * @author Wu Yuping
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if ("match".equals(args[0])) {
            if (args.length >= 7) {
                ImageMatcher.match(args[1], args[2], Integer.valueOf(args[3]), Integer.valueOf(args[4]),
                        Integer.valueOf(args[5]), Integer.valueOf(args[6]));
            } else if (args.length >= 5) {
                ImageMatcher.match(args[1], args[2], Integer.valueOf(args[3]), Integer.valueOf(args[4]), 9999, 9999);
            } else if (args.length >= 3) {
                ImageMatcher.match(args[1], args[2]);
            }
        } else if ("blackwhite".equals(args[0])) {
            BlackWhite.process(args[1], args[2]);
        }
    }
}
