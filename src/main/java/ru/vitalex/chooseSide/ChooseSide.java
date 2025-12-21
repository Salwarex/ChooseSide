package ru.vitalex.chooseSide;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vitalex.chooseSide.service.DataHandler;
import ru.vitalex.chooseSide.service.pool.PlayerDataPool;
import ru.vitalex.chooseSide.service.pool.SidePool;
import ru.vitalex.chooseSide.userInterface.MainCommand;
import ru.vitalex.chooseSide.userInterface.OpenInterfaceListener;
import ru.waxera.beeLib.BeeLib;

import ru.waxera.beeLib.utils.data.database.DatabaseType;
import ru.waxera.beeLib.utils.message.Message;
import ru.waxera.beeLib.utils.specials.language.Language;

public final class ChooseSide extends JavaPlugin {

    private static ChooseSide instance;
    private DataHandler dataHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        BeeLib.setPlugin(instance, new Language[]{Language.ENGLISH, Language.RUSSIAN});

        Message.send(instance, "Plugin is running!");

        DatabaseType type = DatabaseType.valueOf(getConfig().getString("database.type", "SQLITE"));
        String prefix = (type == DatabaseType.SQLITE ? ChooseSide.getInstance().getDataFolder().getAbsolutePath() + "/" : "");
        System.out.println(prefix + getConfig().getString("database.connection", prefix + "database.db"));
        dataHandler = new DataHandler(
                type,
                prefix + getConfig().getString("database.connection", prefix + "database.db"),
                getConfig().getString("database.user", null),
                getConfig().getString("database.password", null)
        );

        poolInit();
        new MainCommand();
        Bukkit.getPluginManager().registerEvents(new OpenInterfaceListener(), this);
    }

    private void poolInit(){
        SidePool.getInstance();
        PlayerDataPool.getInstance();
    }


    public static ChooseSide getInstance() {
        return instance;
    }

    public DataHandler getDataHandler(){
        return dataHandler;
    }
}
