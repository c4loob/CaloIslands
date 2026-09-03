package me.calo.islands.command;

import me.calo.islands.CaloIslandsPlugin;
import me.calo.islands.core.MessageService;
import me.calo.islands.core.event.EventManager;
import me.calo.islands.core.event.GameEvent;
import me.calo.islands.events.cerberus.CerberusEvent;
import me.calo.islands.events.goldenchest.GoldenChestEvent;
import me.calo.islands.events.slime.SlimeEvent;
import me.calo.islands.goblin.GoblinItemService;
import me.calo.islands.goblin.GoblinVillageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CaloIslandsCommand
        implements CommandExecutor, TabCompleter {

    private final CaloIslandsPlugin plugin;
    private final MessageService messages;

    private final GoblinVillageService goblin;
    private final GoblinItemService items;

    private final EventManager events;
    private final SlimeEvent slime;
    private final CerberusEvent cerberus;
    private final GoldenChestEvent goldenChest;

    public CaloIslandsCommand(
            CaloIslandsPlugin plugin,
            MessageService messages,
            GoblinVillageService goblin,
            GoblinItemService items,
            EventManager events,
            SlimeEvent slime,
            CerberusEvent cerberus,
            GoldenChestEvent goldenChest
    ) {
        this.plugin = plugin;
        this.messages = messages;
        this.goblin = goblin;
        this.items = items;
        this.events = events;
        this.slime = slime;
        this.cerberus = cerberus;
        this.goldenChest = goldenChest;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!sender.hasPermission("caloislands.admin")) {
            messages.send(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            help(sender);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                plugin.reloadAll();
                messages.send(sender, "reloaded");
            }

            case "status" ->
                    status(sender);

            case "goblin" ->
                    goblin(sender, args);

            case "event" ->
                    event(sender, args);

            case "slime" ->
                    slime(sender, args);

            case "cerberus" ->
                    cerberus(sender, args);

            case "chest", "goldenchest" ->
                    chest(sender, args);

            default ->
                    help(sender);
        }

        return true;
    }

    private void event(
            CommandSender sender,
            String[] args
    ) {
        if (args.length < 3) {
            sender.sendMessage(
                    messages.color(
                            "&eUso: &f/ci event <start|stop|status> <slime|cerberus|goldenchest>"
                    )
            );
            return;
        }

        String action =
                args[1].toLowerCase(Locale.ROOT);

        String id =
                args[2].toLowerCase(Locale.ROOT);

        GameEvent event =
                events.get(id);

        if (event == null) {
            sender.sendMessage(
                    messages.color(
                            "&cEvento desconocido: &f" + id
                    )
            );
            return;
        }

        switch (action) {
            case "start" -> {
                boolean started =
                        events.start(id);

                sender.sendMessage(
                        messages.color(
                                started
                                        ? "&aEvento iniciado: &f" + id
                                        : "&cNo se pudo iniciar: &f" + id
                        )
                );
            }

            case "stop" -> {
                events.stop(id);

                sender.sendMessage(
                        messages.color(
                                "&eEvento detenido: &f" + id
                        )
                );
            }

            case "status" ->
                    sender.sendMessage(
                            messages.color(
                                    "&6" + id
                                            + " &8» &f"
                                            + event.state()
                            )
                    );

            default ->
                    sender.sendMessage(
                            messages.color(
                                    "&eUso: &f/ci event <start|stop|status> <evento>"
                            )
                    );
        }
    }

    private void slime(
            CommandSender sender,
            String[] args
    ) {
        if (args.length < 2) {
            sender.sendMessage(
                    messages.color(
                            "&e/ci slime setspawn"
                    )
            );
            return;
        }

        if (args[1].equalsIgnoreCase("setspawn")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(
                        messages.prefix()
                                + "Solo un jugador puede usar este comando."
                );
                return;
            }

            plugin.getConfig().set(
                    "events.slime.location.world",
                    player.getWorld().getName()
            );

            plugin.getConfig().set(
                    "events.slime.location.x",
                    player.getLocation().getX()
            );

            plugin.getConfig().set(
                    "events.slime.location.y",
                    player.getLocation().getY()
            );

            plugin.getConfig().set(
                    "events.slime.location.z",
                    player.getLocation().getZ()
            );

            plugin.saveConfig();

            sender.sendMessage(
                    messages.color(
                            "&dSlime spawn &aguardado."
                    )
            );
        }
    }

    private void cerberus(
            CommandSender sender,
            String[] args
    ) {
        if (args.length < 2) {
            sender.sendMessage(
                    messages.color(
                            "&e/ci cerberus setspawn"
                    )
            );
            return;
        }

        if (args[1].equalsIgnoreCase("setspawn")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(
                        messages.prefix()
                                + "Solo un jugador puede usar este comando."
                );
                return;
            }

            plugin.getConfig().set(
                    "events.cerberus.location.world",
                    player.getWorld().getName()
            );

            plugin.getConfig().set(
                    "events.cerberus.location.x",
                    player.getLocation().getX()
            );

            plugin.getConfig().set(
                    "events.cerberus.location.y",
                    player.getLocation().getY()
            );

            plugin.getConfig().set(
                    "events.cerberus.location.z",
                    player.getLocation().getZ()
            );

            plugin.saveConfig();

            sender.sendMessage(
                    messages.color(
                            "&cCerberus spawn &aguardado."
                    )
            );
        }
    }

    private void chest(
            CommandSender sender,
            String[] args
    ) {
        if (args.length < 2) {
            sender.sendMessage(
                    messages.color(
                            "&e/ci chest points <jugador> <cantidad>"
                    )
            );
            return;
        }

        if (!args[1].equalsIgnoreCase("points")) {
            return;
        }

        if (args.length < 4) {
            sender.sendMessage(
                    messages.color(
                            "&e/ci chest points <jugador> <cantidad>"
                    )
            );
            return;
        }

        Player target =
                plugin.getServer().getPlayerExact(args[2]);

        if (target == null) {
            sender.sendMessage(
                    messages.color(
                            "&cJugador no encontrado."
                    )
            );
            return;
        }

        int amount;

        try {
            amount =
                    Integer.parseInt(args[3]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(
                    messages.color(
                            "&cCantidad inválida."
                    )
            );
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(
                    messages.color(
                            "&cLa cantidad debe ser mayor a 0."
                    )
            );
            return;
        }

        goldenChest.addPoints(
                target,
                amount
        );

        sender.sendMessage(
                messages.color(
                        "&6Golden Chest &8» &f"
                                + target.getName()
                                + " ahora tiene &e"
                                + goldenChest.points(
                                target.getUniqueId()
                        )
                                + " puntos."
                )
        );
    }

    private void goblin(
            CommandSender sender,
            String[] args
    ) {
        if (args.length < 2) {
            help(sender);
            return;
        }

        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "start" -> {
                if (goblin.start()) {
                    messages.send(
                            sender,
                            "goblin-started"
                    );
                } else {
                    messages.send(
                            sender,
                            "goblin-already-active"
                    );
                }
            }

            case "stop" -> {
                if (goblin.stop()) {
                    messages.send(
                            sender,
                            "goblin-stopped"
                    );
                } else {
                    messages.send(
                            sender,
                            "goblin-not-active"
                    );
                }
            }

            case "status" ->
                    goblinStatus(sender);

            case "setcenter" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(
                            messages.prefix()
                                    + "Solo un jugador puede usar este comando."
                    );
                    return;
                }

                goblin.setCenter(
                        player.getLocation()
                );

                messages.send(
                        sender,
                        "goblin-center-set"
                );
            }

            case "give" ->
                    give(sender, args);

            default ->
                    help(sender);
        }
    }

    private void give(
            CommandSender sender,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            return;
        }

        if (args.length < 4) {
            sender.sendMessage(
                    messages.prefix()
                            + "/ci goblin give <piel|colmillo> <cantidad>"
            );
            return;
        }

        int amount;

        try {
            amount =
                    Math.max(
                            1,
                            Integer.parseInt(args[3])
                    );
        } catch (NumberFormatException exception) {
            amount = 1;
        }

        if (args[2].equalsIgnoreCase("piel")) {
            player.getInventory().addItem(
                    items.piel(amount)
            );
        } else if (args[2].equalsIgnoreCase("colmillo")) {
            player.getInventory().addItem(
                    items.colmillo(amount)
            );
        }
    }

    private void status(
            CommandSender sender
    ) {
        sender.sendMessage(
                messages.color(
                        "&6&lCaloIslands"
                )
        );

        goblinStatus(sender);

        sender.sendMessage(
                messages.color(
                        "&dSlime: &f"
                                + slime.state()
                )
        );

        sender.sendMessage(
                messages.color(
                        "&cCerberus: &f"
                                + cerberus.state()
                )
        );

        sender.sendMessage(
                messages.color(
                        "&6Golden Chest: &f"
                                + goldenChest.state()
                )
        );

        sender.sendMessage(
                messages.color(
                        "&8Necromancer: &7Pendiente"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&8Volcanic: &7Pendiente"
                )
        );
    }

    private void goblinStatus(
            CommandSender sender
    ) {
        if (!goblin.isActive()) {
            sender.sendMessage(
                    messages.color(
                            "&2Goblin Village: &cINACTIVA"
                    )
            );
            return;
        }

        long seconds =
                goblin.remainingSeconds();

        sender.sendMessage(
                messages.color(
                        "&2Goblin Village: &a"
                                + goblin.state()
                                + " &8| &f"
                                + (seconds / 60)
                                + "m "
                                + (seconds % 60)
                                + "s &8| &f"
                                + goblin.participantCount()
                                + " participantes"
                )
        );
    }

    private void help(
            CommandSender sender
    ) {
        sender.sendMessage(
                messages.color(
                        "&6&lCaloIslands"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci status"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci reload"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci goblin start|stop|status|setcenter"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci goblin give <piel|colmillo> <cantidad>"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci slime setspawn"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci cerberus setspawn"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci event start <slime|cerberus|goldenchest>"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci event stop <slime|cerberus|goldenchest>"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci event status <slime|cerberus|goldenchest>"
                )
        );

        sender.sendMessage(
                messages.color(
                        "&f/ci chest points <jugador> <cantidad>"
                )
        );
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (args.length == 1) {
            return match(
                    args[0],
                    List.of(
                            "status",
                            "reload",
                            "goblin",
                            "event",
                            "slime",
                            "cerberus",
                            "chest"
                    )
            );
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("goblin")) {
            return match(
                    args[1],
                    List.of(
                            "start",
                            "stop",
                            "status",
                            "setcenter",
                            "give"
                    )
            );
        }

        if (args.length == 3
                && args[0].equalsIgnoreCase("goblin")
                && args[1].equalsIgnoreCase("give")) {
            return match(
                    args[2],
                    List.of(
                            "piel",
                            "colmillo"
                    )
            );
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("event")) {
            return match(
                    args[1],
                    List.of(
                            "start",
                            "stop",
                            "status"
                    )
            );
        }

        if (args.length == 3
                && args[0].equalsIgnoreCase("event")) {
            return match(
                    args[2],
                    List.of(
                            "slime",
                            "cerberus",
                            "goldenchest"
                    )
            );
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("slime")) {
            return match(
                    args[1],
                    List.of("setspawn")
            );
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("cerberus")) {
            return match(
                    args[1],
                    List.of("setspawn")
            );
        }

        if (args.length == 2
                && (args[0].equalsIgnoreCase("chest")
                || args[0].equalsIgnoreCase("goldenchest"))) {
            return match(
                    args[1],
                    List.of("points")
            );
        }

        return List.of();
    }

    private List<String> match(
            String input,
            List<String> values
    ) {
        String lower =
                input.toLowerCase(Locale.ROOT);

        List<String> output =
                new ArrayList<>();

        for (String value : values) {
            if (value.startsWith(lower)) {
                output.add(value);
            }
        }

        return output;
    }
}