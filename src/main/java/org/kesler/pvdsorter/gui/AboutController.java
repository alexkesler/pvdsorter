package org.kesler.pvdsorter.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Window;
import org.kesler.pvdsorter.Version;


/**
 * Created by alex on 13.12.14.
 */
public class AboutController extends AbstractController {

    @FXML protected Label versionLabel;
    @FXML protected Label releaseDateLabel;


    @Override
    public void show(Window owner) {
        super.show(owner, "О программе");
    }

    @Override
    protected void updateContent() {
        versionLabel.setText(Version.getVersion());
        releaseDateLabel.setText(Version.getReleaseDate());
    }
}
