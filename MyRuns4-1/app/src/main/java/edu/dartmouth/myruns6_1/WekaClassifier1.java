package edu.dartmouth.myruns6_1;

/**
 * Created by Zizi on 2/15/2017.
 */

public class WekaClassifier1 {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier1.N1727517f10(i);
        return p;
    }
    static double N1727517f10(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 73.944244) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 73.944244) {
            p = WekaClassifier1.N165774b911(i);
        }
        return p;
    }
    static double N165774b911(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 9.924963) {
            p = WekaClassifier1.N47b0292412(i);
        } else if (((Double) i[64]).doubleValue() > 9.924963) {
            p = WekaClassifier1.N6429a87616(i);
        }
        return p;
    }
    static double N47b0292412(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 123.91129) {
            p = WekaClassifier1.N7e5d1f4013(i);
        } else if (((Double) i[0]).doubleValue() > 123.91129) {
            p = 1;
        }
        return p;
    }
    static double N7e5d1f4013(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 18.300656) {
            p = WekaClassifier1.N13aee39014(i);
        } else if (((Double) i[4]).doubleValue() > 18.300656) {
            p = 0;
        }
        return p;
    }
    static double N13aee39014(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() <= 2.970093) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() > 2.970093) {
            p = WekaClassifier1.N3ede0d0815(i);
        }
        return p;
    }
    static double N3ede0d0815(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 3.054612) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() > 3.054612) {
            p = 1;
        }
        return p;
    }
    static double N6429a87616(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 691.325893) {
            p = WekaClassifier1.N6af30e5917(i);
        } else if (((Double) i[0]).doubleValue() > 691.325893) {
            p = 2;
        }
        return p;
    }
    static double N6af30e5917(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 464.830875) {
            p = WekaClassifier1.N7b332be918(i);
        } else if (((Double) i[0]).doubleValue() > 464.830875) {
            p = 2;
        }
        return p;
    }
    static double N7b332be918(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 12.585503) {
            p = WekaClassifier1.N5c880d2f19(i);
        } else if (((Double) i[64]).doubleValue() > 12.585503) {
            p = 2;
        }
        return p;
    }
    static double N5c880d2f19(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 2;
        } else if (((Double) i[15]).doubleValue() <= 1.915994) {
            p = 2;
        } else if (((Double) i[15]).doubleValue() > 1.915994) {
            p = WekaClassifier1.N6819b81e20(i);
        }
        return p;
    }
    static double N6819b81e20(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 337.533536) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 337.533536) {
            p = 1;
        }
        return p;
    }
}
