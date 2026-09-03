package me.calo.islands;

import me.calo.islands.command.CaloIslandsCommand;
import me.calo.islands.combat.BossDamageListener;
import me.calo.islands.combat.BossDeathListener;
import me.calo.islands.combat.BossParticipationService;
import me.calo.islands.core.MessageService;
import me.calo.islands.core.event.EventManager;
import me.calo.islands.data.DatabaseManager;
import me.calo.islands.events.cerberus.CerberusEvent;
import me.calo.islands.events.goldenchest.GoldenChestEvent;
import me.calo.islands.events.slime.SlimeEvent;
import me.calo.islands.goblin.GoblinDropListener;
import me.calo.islands.goblin.GoblinItemService;
import me.calo.islands.goblin.GoblinVillageService;
import me.calo.islands.integration.mythicmobs.MythicMobsHook;
import me.calo.islands.rewards.RewardItemService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class CaloIslandsPlugin extends JavaPlugin {

    private MessageService messages;
    private DatabaseManager database;

    private GoblinItemService goblinItems;
    private GoblinVillageService goblinVillage;

    private EventManager eventManager;
    private MythicMobsHook mythicMobs;
    private BossParticipationService bossParticipation;
    private RewardItemService rewardItems;

    private SlimeEvent slimeEvent;
    private CerberusEvent cerberusEvent;
    private GoldenChestEvent goldenChestEvent;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);

        this.messages = new MessageService(this);

        if (!initializeDatabase()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        /*
         * Existing Goblin system
         */
        this.goblinItems = new GoblinItemService(this);
        this.goblinVillage = new GoblinVillageService(
                this,
                messages,
                goblinItems
        );

        getServer().getPluginManager().registerEvents(
                new GoblinDropListener(this, goblinVillage),
                this
        );

        /*
         * Shared event infrastructure
         */
        this.mythicMobs = new MythicMobsHook(this);
        this.bossParticipation = new BossParticipationService(this);
        this.rewardItems = new RewardItemService(this);

        this.eventManager = new EventManager(this);

        /*
         * Main events
         */
        this.slimeEvent = new SlimeEvent(
                this,
                mythicMobs,
                bossParticipation
        );

        this.cerberusEvent = new CerberusEvent(
                this,
                mythicMobs,
                bossParticipation
        );

        this.goldenChestEvent = new GoldenChestEvent(this);

        eventManager.register(slimeEvent);
        eventManager.register(cerberusEvent);
        eventManager.register(goldenChestEvent);

        /*
         * Combat listeners
         */
        getServer().getPluginManager().registerEvents(
                new BossDamageListener(bossParticipation),
                this
        );

        getServer().getPluginManager().registerEvents(
                new BossDeathListener(
                        bossParticipation,
                        slimeEvent,
                        cerberusEvent,
                        rewardItems
                ),
                this
        );

        /*
         * Command
         */
        PluginCommand command = getCommand("caloislands");

        if (command != null) {
            CaloIslandsCommand executor = new CaloIslandsCommand(
                    this,
                    messages,
                    goblinVillage,
                    goblinItems,
                    eventManager,
                    slimeEvent,
                    cerberusEvent,
                    goldenChestEvent
            );

            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        /*
         * Schedulers
         */
        goblinVillage.startScheduler();
        eventManager.startScheduler();

        getLogger().info(
                "SQLite conectado: "
                        + database.getDatabaseFile().getName()
        );

        getLogger().info(
                "MythicMobs: "
                        + (mythicMobs.isAvailable()
                        ? "CONECTADO"
                        : "NO DISPONIBLE")
        );

        getLogger().info(
                "Eventos registrados: "
                        + eventManager.events().size()
        );

        getLogger().info(
                "CaloIslands habilitado correctamente."
        );
    }

    private boolean initializeDatabase() {
        try {
            this.database = new DatabaseManager(this);
            this.database.initialize();
            return true;

        } catch (SQLException | RuntimeException exception) {
            getLogger().severe(
                    "No se pudo inicializar la base de datos de CaloIslands."
            );

            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDisable() {
        if (eventManager != null) {
            eventManager.shutdown();
        }

        if (bossParticipation != null) {
            bossParticipation.clear();
        }

        if (goblinVillage != null) {
            goblinVillage.shutdown();
        }
    }

    public void reloadAll() {
        reloadConfig();
        messages.reload();

        if (goblinVillage != null) {
            goblinVillage.reload();
        }
    }

    public DatabaseManager database() {
        return database;
    }

    public EventManager events() {
        return eventManager;
    }

    public MythicMobsHook mythicMobs() {
        return mythicMobs;
    }

    public BossParticipationService bossParticipation() {
        return bossParticipation;
    }

    public RewardItemService rewardItems() {
        return rewardItems;
    }

    public SlimeEvent slimeEvent() {
        return slimeEvent;
    }

    public CerberusEvent cerberusEvent() {
        return cerberusEvent;
    }

    public GoldenChestEvent goldenChestEvent() {
        return goldenChestEvent;
    }
}