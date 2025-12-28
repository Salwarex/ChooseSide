package ru.vitalex.chooseSide.service;

import org.bukkit.Material;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.pool.SidePool;
import ru.vitalex.chooseSide.service.variables.PlayerDataVariables;
import ru.vitalex.chooseSide.service.variables.SideVariable;
import ru.waxera.beeLib.utils.data.database.DatabaseType;
import ru.waxera.beeLib.utils.data.database.query.LogicalOperator;
import ru.waxera.beeLib.utils.data.database.query.QueryWherePair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class DataHandler extends ru.waxera.beeLib.utils.data.DataHandler {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final static ChooseSide PLUGIN = ChooseSide.getInstance();

    public DataHandler(DatabaseType type, String connectionString, String user, String password) {
        super(type, connectionString, user, password);

        String autoIncrement = (type == DatabaseType.MYSQL) ? "AUTO_INCREMENT" : "AUTOINCREMENT";
        String dateTimeType = (type == DatabaseType.MYSQL) ? "DATETIME" : "TEXT";
        String booleanType = (type == DatabaseType.MYSQL) ? "TINYINT(1)" : "INTEGER";

        if (type == DatabaseType.SQLITE || type == DatabaseType.MYSQL){
            database.createTable("sides",
                    "uuid VARCHAR(36) PRIMARY KEY NOT NULL," +
                            "name VARCHAR(64) NOT NULL," +
                            "description TEXT NOT NULL," +
                            "symbol TEXT," +
                            "active INTEGER NOT NULL DEFAULT 1," +
                            "prefix VARCHAR(10) NOT NULL, " +
                            "created_at " + dateTimeType + " NOT NULL, " +
                            "dsrv_role_id VARCHAR(32), " +
                            "welcome_message TEXT, " +
                            "balance_coef REAL NOT NULL, "
            );

            database.createTable("players",
                    "uuid VARCHAR(36) PRIMARY KEY NOT NULL, " +
                            "side_uuid VARCHAR(36) NOT NULL, " +
                            "chose_at " + dateTimeType + " NOT NULL, " +
                            "change_side_tokens INTEGER NOT NULL DEFAULT 0, "+
                            "already_took_online_tokens " + dateTimeType + ", " +
                            "FOREIGN KEY (side_uuid) REFERENCES sides(uuid)"
            );

            database.createTable("stats",
                    "side_uuid VARCHAR(36) NOT NULL, " +
                            "statistic VARCHAR(64) NOT NULL, " +
                            "value REAL NOT NULL DEFAULT 0, " +
                            "PRIMARY KEY (side_uuid, statistic), " +
                            "FOREIGN KEY (side_uuid) REFERENCES sides(uuid)"
            );
        }
    }

    public int getCount(String table){
        return database.count(table);
    }

    public void initSides(){
        ArrayList<ArrayList<Object>> dataset = database.getDataObjects("*",
                new String[]{
                        "uuid",
                        "name",
                        "description",
                        "symbol",
                        "prefix",
                        "created_at",
                        "dsrv_role_id",
                        "welcome_message",
                        "balance_coef"
                },
                "sides",
                new QueryWherePair(null, "active", 1));

        if(dataset != null){
            for(ArrayList<Object> object : dataset){
                UUID uuid = UUID.fromString((String) object.getFirst());
                String name = (String) object.get(1);
                String description = (String) object.get(2);
                Material symbol = Material.valueOf((String) object.get(3));
                String prefix = ((String) object.get(4));
                String createdAtStr = ((String) object.get(5));
                LocalDateTime createdAt = LocalDateTime.parse(createdAtStr, DATE_TIME_FORMATTER);
                String dsrvRoleId = (String) object.get(6);
                String welcomeMessage = (String) object.get(7);
                double balanceCoef = (double) object.get(8);

                Side side = new Side(uuid, name, description, symbol, prefix, true, createdAt, null, dsrvRoleId, welcomeMessage, balanceCoef);
                loadSidesStatistics(side);
            }
        }
    }

    private void loadSidesStatistics(Side side){
        ArrayList<ArrayList<Object>> dataset = database.getDataObjects("*",
                new String[]{
                        "statistic",
                        "value"
                },
                "stats",
                new QueryWherePair(null, "side_uuid", side.getUuid()));

        SideStatistics stats = side.getStatistics();

        if(dataset != null){
            for(ArrayList<Object> object : dataset){
                RecordingStatistic stat = RecordingStatistic.valueOf((String) object.getFirst());
                Double value = (Double) object.get(1);
                stats.setValue(stat, value);
            }
        }
    }

    public void insertSide(Side side){
        if(side == null) throw new RuntimeException("[DataHandler.insertSide] Side is null!");

        String createdAt = DATE_TIME_FORMATTER.format(side.getCreatedAt());
        UUID uuid = side.getUuid();

        database.insert("sides",
                "uuid, name, description, symbol, prefix, created_at, dsrv_role_id, welcome_message, balance_coef",
                uuid.toString(),
                side.getName(),
                side.getDescription(),
                side.getSymbol(),
                side.getPrefix(),
                createdAt,
                side.getDsrvRoleId(),
                side.getWelcomeMessage(),
                side.getBalanceCoef()
        );

        for(RecordingStatistic stat : RecordingStatistic.values()){
            database.insert("stats",
                    "side_uuid, statistic, value",
                    uuid, stat, 0.0
            );
        }
    }

    public void saveSide(Side side){
        if (side == null) throw new RuntimeException("[DataHandler.saveSide] side is null");

        QueryWherePair whereSide = new QueryWherePair(null, "uuid", side.getUuid());
        QueryWherePair whereSideStat = new QueryWherePair(null, "side_uuid", side.getUuid());

        Set<SideVariable> lastChangedSide = side.getLastSessionChanged();
        SideStatistics statistics = side.getStatistics();
        Set<RecordingStatistic> lastChangedStat = statistics.getLastSessionChanged();

        if(lastChangedSide != null){
            for(SideVariable variable : lastChangedSide){
                if(variable == SideVariable.NAME){
                    database.updateData("sides", "name", side.getName(),
                            whereSide);
                } else if (variable == SideVariable.DESCRIPTION) {
                    database.updateData("sides", "description", side.getDescription(),
                            whereSide);
                } else if (variable == SideVariable.ACTIVE) {
                    database.updateData("sides", "active", side.isActive() ? 1 : 0,
                            whereSide);
                } else if (variable == SideVariable.SYMBOL) {
                    database.updateData("sides", "symbol", side.getSymbol(),
                            whereSide);
                } else if (variable == SideVariable.PREFIX) {
                    database.updateData("sides", "symbol", side.getPrefix(),
                            whereSide);
                } else if (variable == SideVariable.DSRV_ROLE_ID) {
                    database.updateData("sides", "dsrv_role_id", side.getDsrvRoleId(),
                            whereSide);
                } else if (variable == SideVariable.WELCOME_MESSAGE) {
                    database.updateData("sides", "welcome_message", side.getWelcomeMessage(),
                            whereSide);
                } else if (variable == SideVariable.BALANCE_COEF) {
                    database.updateData("sides", "balance_coef", side.getBalanceCoef(),
                            whereSide);
                }
            }
            lastChangedSide.clear();
        }

        if(lastChangedStat != null){
            for(RecordingStatistic variable : lastChangedStat){
                QueryWherePair whereStat = new QueryWherePair(LogicalOperator.AND, "statistic",
                        variable.toString());

                database.updateData("stats", "value", statistics.getValue(variable),
                        whereSideStat, whereStat);
            }
            lastChangedStat.clear();
        }
    }


    public void initPlayerData(){
        ArrayList<ArrayList<Object>> dataset = database.getDataObjects("*",
                new String[]{
                        "uuid",
                        "side_uuid",
                        "chose_at",
                        "change_side_tokens",
                        "already_took_online_tokens"
                },
                "players");

        if(dataset != null){
            for(ArrayList<Object> object : dataset){
                UUID uuid = UUID.fromString((String) object.getFirst());
                Side side = SidePool.getInstance().get(UUID.fromString((String) object.get(1)));
                String choseAtStr = ((String) object.get(2));
                LocalDateTime choseAt = LocalDateTime.parse(choseAtStr, DATE_TIME_FORMATTER);
                int changeSideTokens = (int) object.get(3);
                int alreadyTookOnlineTokens = (int) object.get(4);

                new PlayerData(uuid, side, choseAt, changeSideTokens, alreadyTookOnlineTokens);
                loadSidesStatistics(side);
            }
        }
    }

    public void insertPlayerData(PlayerData playerData){
        if(playerData == null) throw new RuntimeException("[DataHandler.insertPlayerData] Player data  is null!");

        String choseAt = DATE_TIME_FORMATTER.format(playerData.getChoseAt());
        UUID uuid = playerData.getUuid();

        database.insert("players",
                "uuid, side_uuid, chose_at, change_side_tokens, already_took_online_tokens",
                uuid.toString(),
                playerData.getSide().getUuid().toString(),
                choseAt,
                playerData.getChangeSideTokens(),
                playerData.getAlreadyTookOnlineTokens()
        );
    }

    public void savePlayerData(PlayerData playerData){
        if (playerData == null) throw new RuntimeException("[DataHandler.savePlayerData] Player data is null");

        QueryWherePair whereSide = new QueryWherePair(null, "uuid", playerData.getUuid());

        Set<PlayerDataVariables> lastChangedSide = playerData.getLastSessionChanged();

        if(lastChangedSide != null){
            for(PlayerDataVariables variable : lastChangedSide){
                if(variable == PlayerDataVariables.SIDE){
                    database.updateData("players", "side_uuid", playerData.getSide().getUuid().toString(),
                            whereSide);
                }
                else if(variable == PlayerDataVariables.TOKENS){
                    database.updateData("players", "change_side_tokens", playerData.getChangeSideTokens(),
                            whereSide);
                }
                else if(variable == PlayerDataVariables.ALREADY_TOOK_TOKENS){
                    database.updateData("players", "already_took_online_tokens", playerData.getAlreadyTookOnlineTokens(),
                            whereSide);
                }
            }
            lastChangedSide.clear();
        }
    }
}
