package me.matthewedevelopment.atheriallib.menu.ui;

//import dev.triumphteam.gui.components.GuiType;
//import dev.triumphteam.gui.guis.Gui;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
//import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public  abstract class AtherialNormalUI <C extends YamlConfig>extends AtherialUI<C> {


//    protected Gui ui;

    public AtherialNormalUI(C c, UIInformation information) {
        super(c, information);
    }

    @Override
    public void open(Player p ) {
//        ui.open(p);
    }

    @Override
    public void update(Player p) {
//        ui.update();
    }

    @Override
    public void createGUI(Player p) {
//        ui = Gui.gui()
//                .title(information.getTitle())
//                .rows(information.getRows())
//                .create();
    }

}
