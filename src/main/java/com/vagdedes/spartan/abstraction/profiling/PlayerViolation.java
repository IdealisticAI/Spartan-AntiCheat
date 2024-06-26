package com.vagdedes.spartan.abstraction.profiling;

import me.vagdedes.spartan.system.Enums;

public class PlayerViolation {

    public final Enums.HackType hackType;
    public final long time;
    public final String information, detection;
    public final int level, identity;

    public PlayerViolation(long time,
                           Enums.HackType hackType,
                           String information,
                           int level,
                           InformationAnalysis analysis) {
        this.hackType = hackType;
        this.time = time;
        this.information = information;
        this.level = level;

        this.detection = analysis.detection;
        this.identity = analysis.identity;
    }

    public PlayerViolation(long time,
                           Enums.HackType hackType,
                           String information,
                           int level) {
        this(
                time,
                hackType,
                information,
                level,
                new InformationAnalysis(hackType, information)
        );
    }

    public int getIgnoredViolations(Enums.DataType dataType) {
        return hackType.getCheck().getIgnoredViolations(dataType, this.identity);
    }

}
