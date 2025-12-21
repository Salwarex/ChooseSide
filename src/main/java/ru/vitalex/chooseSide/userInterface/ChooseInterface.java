package ru.vitalex.chooseSide.userInterface;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.vitalex.chooseSide.ChooseSide;
import ru.vitalex.chooseSide.service.PlayerData;
import ru.vitalex.chooseSide.service.Side;
import ru.vitalex.chooseSide.service.pool.SidePool;
import ru.waxera.beeLib.utils.gui.container.ContainerInterface;
import ru.waxera.beeLib.utils.gui.container.SinglePageInterface;
import ru.waxera.beeLib.utils.gui.questionnaire.Question;
import ru.waxera.beeLib.utils.gui.questionnaire.Questionnaire;
import ru.waxera.beeLib.utils.gui.questionnaire.QuestionnairePool;
import ru.waxera.beeLib.utils.message.Message;
import ru.waxera.beeLib.utils.specials.items.ItemStackBuilder;
import ru.waxera.beeLib.utils.specials.string.StringUtils;

import java.util.*;

public class ChooseInterface {
    private final static ChooseSide PLUGIN = ChooseSide.getInstance();


    public static void open(Player player){
        int size = 27;
        SinglePageInterface gui = new SinglePageInterface(null,
                StringUtils.format("Выберите свою сторону!", PLUGIN),
                size, false);

        SidePool pool = SidePool.getInstance();

        Set<UUID> keys = pool.keys();
        List<Side> sides = new ArrayList<>();
        for(UUID uuid : keys){
            sides.add(pool.get(uuid));
        }

        if(sides.size() == 2){
            ArrayList<Integer> bgs = new ArrayList<>();
            Collections.addAll(bgs, 3, 4, 5, 12, 13, 14, 21, 22, 23);
            gui.setBackground(bgs, Material.GRAY_STAINED_GLASS_PANE);

            Side left = sides.getFirst();
            Side right = sides.getLast();

            ItemStack leftFiller = createSideIs(left);
            ItemStack rightFiller = createSideIs(right);

            for(int i = 0; i < size; i++){
                if(i % 9 <= 2) gui.setItem(i, leftFiller, (p, e) -> confirm(p, left, gui));
                else if (i % 9 >= 6) gui.setItem(i, rightFiller, (p, e) -> confirm(p, right, gui));
            }
        }
        else{
            for(Side side : sides){
                ItemStack stack = createSideIs(side);
                gui.addItem(stack, (p, e) -> confirm(p, side, gui));
            }
        }

        gui.open(player);
    }

    private static void confirm(Player player, Side side, ContainerInterface gui){
        Question confirmQuestion = new Question(PLUGIN, "confirm",
                """
                       \
                       \s
                       &7Подтвердите, что вы хотите присоединиться к &e%s&7. Отправьте:
                       \
                       \s
                        &7- "&e+&7", если правильно.
                        &7- "&e-&7", если неправильно.
                       \
                       \s
                       """.formatted(side.getName()));

        new Questionnaire(
                PLUGIN,
                player,
                (pl, ev) -> {
                    if (!confirmQuestion.getAnswer().equals("+")) {
                        Message.send(PLUGIN, player,
                                """
                                        \
                                        \s
                                        &7Давайте попробуем ещё раз.
                                        \
                                        \s
                                        """);
                        QuestionnairePool pool = QuestionnairePool.getInstance();
                        pool.remove(pl);
                        gui.open(pl);
                    } else {
                        String name = side.getName();
                        Message.send(PLUGIN, player,
                                """
                                        \
                                        \s
                                        &7Вы успешно присоединились к &e%s&7.
                                        \
                                        \s
                                        """.formatted(name));
                        PlayerData.create(pl, side);
                    }
                },
                Sound.BLOCK_NOTE_BLOCK_PLING,
                null,
                false,
                confirmQuestion
        );
    }

    private static ItemStack createSideIs(Side side){
        ItemStackBuilder builder = new ItemStackBuilder(side.getSymbol(), 1);;
        builder.setLore(PLUGIN,
                """
                &7Нажав здесь, Вы присоединитесь к стороне "&e%s"! \
                &fОписание: \
                %s \
                \
                Обратите внимание, что Вы не сможете сменить сторону после нажатия!
                """.formatted(side.getName(), normalizeString(side.getDescription(), 100)));

        builder.setName(PLUGIN, "&e%s".formatted(side.getName()));
        return builder.get();
    }

    public static String normalizeString(String text, int maxLength) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxLength) {
                if (!line.isEmpty()) {
                    result.append(line).append("\n");
                    line = new StringBuilder();
                }

                while (word.length() > maxLength) {
                    result.append(word, 0, maxLength).append("\n");
                    word = word.substring(maxLength);
                }
            }
            if (!line.isEmpty()) {
                line.append(" ");
            }
            line.append(word);
        }

        if (!line.isEmpty()) {
            result.append(line);
        }

        return result.toString();
    }
}
