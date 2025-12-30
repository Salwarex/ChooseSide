package ru.vitalex.chooseSide;

import github.scarsz.discordsrv.DiscordSRV;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.vitalex.chooseSide.service.DataHandler;
import ru.vitalex.chooseSide.service.pool.PlayerDataPool;
import ru.vitalex.chooseSide.service.pool.SidePool;
import ru.vitalex.chooseSide.userInterface.MainCommand;
import ru.vitalex.chooseSide.userInterface.OpenInterfaceListener;
import ru.vitalex.chooseSide.userInterface.Registrator;
import ru.waxera.beeLib.BeeLib;

import ru.waxera.beeLib.utils.data.database.DatabaseType;
import ru.waxera.beeLib.utils.message.Message;
import ru.waxera.beeLib.utils.specials.language.Language;

public final class ChooseSide extends JavaPlugin {

    private static ChooseSide instance;
    private DataHandler dataHandler;
    private LuckPerms luckPerms;
    private DiscordSRV discordSRV;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadApi();
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
        Registrator.start();
        Bukkit.getPluginManager().registerEvents(new OpenInterfaceListener(), this);

        DiscordSRV.getPlugin();
    }

    private void poolInit(){
        SidePool.getInstance();
        PlayerDataPool.getInstance();

        dataHandler.initSides();
        dataHandler.initPlayerData();
    }

    private void loadApi(){
        RegisteredServiceProvider<LuckPerms> providerLp = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (providerLp == null) {
            throw new RuntimeException("LuckPerms не установлен или не загружен!");
        }
        luckPerms = providerLp.getProvider();

        try{
            discordSRV = DiscordSRV.getPlugin();
        } catch (RuntimeException e) {
            throw new RuntimeException("DiscordSRV не установлен или не загружен!");
        }
    }


    public static ChooseSide getInstance() {
        return instance;
    }

    public DataHandler getDataHandler(){
        return dataHandler;
    }

    public LuckPerms getLuckPerms() { return luckPerms; }

    public DiscordSRV getDiscordSRV(){ return discordSRV; }
}
