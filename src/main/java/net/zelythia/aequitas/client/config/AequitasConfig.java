package net.zelythia.aequitas.client.config;

public class AequitasConfig {
    public static final SimpleConfig config;


    static{
        config = SimpleConfig.of("aequitas").provider(AequitasConfig::defaultconfig).request();
    }

    public static String defaultconfig(String filename){
        return "#Aequitas config\n" +
                "#Constantly shows the essence value as a tooltip\n" +
                "showTooltip=false\n" +
                "";
    }
}
