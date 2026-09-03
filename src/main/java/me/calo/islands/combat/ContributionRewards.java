package me.calo.islands.combat;

public final class ContributionRewards {

    private ContributionRewards() {
    }

    public static int materialAmount(double percentage) {
        if (percentage >= 5.0) {
            return 3;
        }

        if (percentage >= 2.5) {
            return 2;
        }

        if (percentage >= 0.75) {
            return 1;
        }

        return 0;
    }
}