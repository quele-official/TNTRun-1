package de.quele.commands.utils;

import de.quele.TNTRun;

public class Constants {


    private final String prefix;

    public Constants(TNTRun tntRun) {
        Config config = TNTRun.getInstance().getConfigFile();
        config.addDefault("prefix", "&6TNTRun &8| &7");
        config.copyDefaults();
        prefix = config.getString("prefix").replace("&", "§");

    }

    public String getPrefix() {
        return prefix;
    }



}