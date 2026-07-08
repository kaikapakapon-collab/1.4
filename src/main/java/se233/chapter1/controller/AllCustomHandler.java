package se233.chapter1.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import se233.chapter1.Launcher;
import se233.chapter1.model.character.BasedCharacter;
import se233.chapter1.model.character.Battlemage;
import se233.chapter1.model.character.MagicalCharacter;
import se233.chapter1.model.character.PhysicalCharacter;
import se233.chapter1.model.character.DamageType;
import se233.chapter1.model.item.Armor;
import se233.chapter1.model.item.BasedEquipment;
import se233.chapter1.model.item.Weapon;
import java.util.ArrayList;

public class AllCustomHandler {

    public static class GenCharacterHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Launcher.unequipAll();
            Launcher.setMainCharacter(GenCharacter.setUpCharacter());
            Launcher.refreshPane();
        }
    }

    public static void onDragDetected(MouseEvent event, BasedEquipment equipment, ImageView imgView) {
        Dragboard db = imgView.startDragAndDrop(TransferMode.ANY);
        db.setDragView(imgView.getImage());
        ClipboardContent content = new ClipboardContent();
        content.put(equipment.DATA_FORMAT, equipment);
        db.setContent(content);
        event.consume();
    }

    public static void onDragOver(DragEvent event, String type) {
        Dragboard dragboard = event.getDragboard();
        BasedEquipment retrievedEquipment = (BasedEquipment)dragboard.getContent(BasedEquipment.DATA_FORMAT);
        if (dragboard.hasContent(BasedEquipment.DATA_FORMAT) && retrievedEquipment.getClass().getSimpleName().equals(type))
            event.acceptTransferModes(TransferMode.MOVE);
    }

    public static void onDragDropped(DragEvent event, Label lbl, StackPane imgGroup) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();
        ArrayList<BasedEquipment> allEquipments = Launcher.getAllEquipments();

        if (dragboard.hasContent(BasedEquipment.DATA_FORMAT)) {
            BasedEquipment retrievedEquipment = (BasedEquipment) dragboard.getContent(BasedEquipment.DATA_FORMAT);
            BasedCharacter character = Launcher.getMainCharacter();

            boolean canEquip = true;
            if (retrievedEquipment instanceof Weapon) {
                Weapon w = (Weapon) retrievedEquipment;
                if (character instanceof Battlemage) {
                    canEquip = true;
                } else if (character instanceof PhysicalCharacter && w.getDamageType() != DamageType.physical) {
                    canEquip = false;
                } else if (character instanceof MagicalCharacter && w.getDamageType() != DamageType.magical) {
                    canEquip = false;
                }
            } else if (retrievedEquipment instanceof Armor) {
                if (character instanceof Battlemage) {
                    canEquip = false;
                }
            }

            if (canEquip) {

                BasedEquipment itemToRemove = null;
                for (BasedEquipment item : allEquipments) {
                    if (item.getName().equals(retrievedEquipment.getName())) {
                        itemToRemove = item;
                        break;
                    }
                }
                if (itemToRemove != null) {
                    allEquipments.remove(itemToRemove);
                }

                if (retrievedEquipment.getClass().getSimpleName().equals("Weapon")) {
                    if (Launcher.getEquippedWeapon() != null)
                        allEquipments.add(Launcher.getEquippedWeapon());
                    Launcher.setEquippedWeapon((Weapon) retrievedEquipment);
                    character.equipWeapon((Weapon) retrievedEquipment);
                } else {
                    if (Launcher.getEquippedArmor() != null)
                        allEquipments.add(Launcher.getEquippedArmor());
                    Launcher.setEquippedArmor((Armor) retrievedEquipment);
                    character.equipArmor((Armor) retrievedEquipment);
                }
                Launcher.setMainCharacter(character);
                Launcher.setAllEquipments(allEquipments);
                Launcher.refreshPane();

                ImageView imgView = new ImageView();
                if (imgGroup.getChildren().size() != 1) {
                    imgGroup.getChildren().remove(1);
                    Launcher.refreshPane();
                }
                lbl.setText(retrievedEquipment.getClass().getSimpleName() + ":\n" + retrievedEquipment.getName());
                imgView.setImage(new Image(Launcher.class.getResource(retrievedEquipment.getImagepath()).toString()));
                imgGroup.getChildren().add(imgView);
                dragCompleted = true;
            }
        }
        event.setDropCompleted(dragCompleted);
    }

    public static void onDragDone(DragEvent event) {
        if (!event.isDropCompleted()) {
            Launcher.refreshPane();
        }
        event.consume();
    }
}