package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.schoolRPG.tools.CircularSelector;
import com.mygdx.schoolRPG.tools.MenuListSelector;

import java.util.ArrayList;

/**
 * Created by Kraft on 04.01.2017.
 */
public class Inventory {
    boolean containerMode;
    ArrayList<Item> items;
    ArrayList<Item> containerItems;
    ArrayList<String> titles;
    ArrayList<String> titles2;
    ArrayList<Texture> sprites;
    ArrayList<Texture> sprites2;
    ArrayList<String> inventoryOpts;
    ArrayList<String> containerOpts;
    ArrayList<String> otherOpts;
    ArrayList<String> inventory2TitlesInLanguages;
    CircularSelector itemsSelector;
    CircularSelector containerItemsSelector;
    MenuListSelector invenoryOptions;
    MenuListSelector containerOptions;
    MenuListSelector otherOptions;
    int language;
    Texture overlay;
    boolean closed;
    public Item droppedItem;
    BitmapFont font;
    boolean justOpened;
    boolean otherIsSelected;
    boolean lastLeftSelected;
    String inventory1Title;
    String inventory2Title;
    boolean releasedAfterChange;
    NPC owner;

    private void updateItems() {
        titles.clear();
        titles2.clear();
        sprites.clear();
        sprites2.clear();
        for (int i = 0; i < items.size(); ++i) {
            titles.add(items.get(i).getName(language));
            sprites.add(items.get(i).icon);
        }
        if (containerMode) {
            for (int i = 0; i < containerItems.size(); ++i) {
                titles2.add(containerItems.get(i).getName(language));
                sprites2.add(containerItems.get(i).icon);
            }
            containerItemsSelector.reset();
        }
        itemsSelector.reset();

    }

    public void reload(int language) {
        this.language = language;
        titles = new ArrayList<String>();
        titles2 = new ArrayList<String>();
        for (int i = 0; i < items.size(); ++i) {
            titles.add(items.get(i).getName(language));
        }
        inventoryOpts = new ArrayList<String>();
        containerOpts = new ArrayList<String>();
        otherOpts = new ArrayList<String>();
        inventoryOpts.add("---");
        containerOpts.add("---");
        if (language == 0) {
            inventory1Title = "Inventory";
            if (containerMode) {
                inventoryOpts.add("Store");
                inventoryOpts.add("Store All");
            } else {
                inventoryOpts.add("Use");
            }
            //inventoryOpts.add("Info");
            if (!containerMode) {
                inventoryOpts.add("Drop");
            }
            containerOpts.add("Take");
            containerOpts.add("Take All");
            //containerOpts.add("Info");
            otherOpts.add("Back");
        } else {
            inventory1Title = "Инвентарь";
            if (containerMode) {
                inventoryOpts.add("Сложить");
                inventoryOpts.add("Сложить Все");
            } else {
                inventoryOpts.add("Использовать");
            }
            //inventoryOpts.add("Инфо");
            if (!containerMode) {
                inventoryOpts.add("Выбросить");
            }
            containerOpts.add("Взять");
            containerOpts.add("Взять Все");
            //containerOpts.add("Инфо");
            otherOpts.add("Назад");
        }
        if (containerMode) {
            inventory2Title = inventory2TitlesInLanguages.get(language);
            for (int i = 0; i < containerItems.size(); ++i) {
                titles2.add(containerItems.get(i).getName(language));
            }
        }
        if (invenoryOptions != null) {
            invenoryOptions.titles = inventoryOpts;
            otherOptions.titles = otherOpts;
            itemsSelector.titles = titles;
            if (containerMode) {
                containerOptions.titles = containerOpts;
                containerItemsSelector.titles = titles2;
            }
        }
    }

    public Inventory(AssetManager assets, BitmapFont font, ArrayList<Item> items, NPC owner, ArrayList<Item> containerItems, ArrayList<String> inventory2TitlesInLanguages, int language) {
        this.owner = owner;
        this.items = items;
        this.font = font;
        this.containerItems = containerItems;
        containerMode = (containerItems != null);
        this.inventory2TitlesInLanguages = inventory2TitlesInLanguages;
        reload(language);
        sprites = new ArrayList<Texture>();
        sprites2 = new ArrayList<Texture>();
        float centerX, centerY, centerX2 = 0, centerY2 = 0, xOffset, yOffset, xOffset2, yOffset2, xOffset3;
        for (int i = 0; i < items.size(); ++i) {
            sprites.add(items.get(i).icon);
        }
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        float width = Gdx.graphics.getWidth()/screenRatioX;
        float height = Gdx.graphics.getHeight()/screenRatioY;
        if (containerMode) {
            for (int i = 0; i < containerItems.size(); ++i) {
                sprites2.add(containerItems.get(i).icon);
            }
            centerX = width/3.0f;
            centerY = height/2.0f + 32;
            centerX2 = 2*width/3.0f;
            centerY2 = height/2.0f + 32;
            xOffset = -width/3.0f;
            yOffset = height/2.0f - 128;
            xOffset2 = width/3.0f;
            yOffset2 = height/2.0f - 128;
            xOffset3 = -width/6.0f;
            containerOptions = new MenuListSelector(containerOpts, assets, "cursor.png", font, 64, -xOffset3, yOffset2, true);
            containerOptions.looping = false;
            containerOptions.enabled = false;
        } else {
            centerX = width/2.0f;
            centerY = height/2.0f + 32;
            xOffset = 0;
            yOffset = height/2.0f - 128;
            xOffset3 = xOffset;
        }
        invenoryOptions = new MenuListSelector(inventoryOpts, assets, "cursor.png", font, 64, xOffset3, yOffset, true);
        invenoryOptions.looping = false;
        otherOptions = new MenuListSelector(otherOpts, assets, "cursor.png", font, 64, 0, yOffset - 128, true);
        otherOptions.looping = false;
        otherOptions.enabled = false;
        itemsSelector = new CircularSelector( titles, sprites, font, centerX, centerY, 128, 64, 2);
        itemsSelector.drawTitles = false;
        if (containerMode) {
            containerItemsSelector = new CircularSelector( titles2, sprites2, font, centerX2, centerY2, 128, 64, 2);
            containerItemsSelector.drawTitles = false;
        }
        overlay = assets.get("inventory_overlay1.png", Texture.class);
        closed = false;
        justOpened = true;
        otherIsSelected = false;
        lastLeftSelected = true;
        releasedAfterChange = true;
    }

    public void addItem(ArrayList<Item> items, Item item) {
        for (int i = 0; i < items.size(); ++i) {
            if (items.get(i).stackable && items.get(i).fileName.equals(item.fileName) && items.get(i).stack < items.get(i).maxStack) {
                items.get(i).stack++;
                return;
            }
        }
        items.add(new Item(item));
    }

    public void draw(SpriteBatch batch, boolean paused) {
        boolean rightIsSelected = false;
        batch.setColor(new Color(1, 1, 1, 0.5f));
        float screenRatioX = Gdx.graphics.getWidth()/1280.0f;
        float screenRatioY = Gdx.graphics.getHeight()/720.0f;
        float width = Gdx.graphics.getWidth()/screenRatioX;
        float height = Gdx.graphics.getHeight()/screenRatioY;
        batch.draw(overlay, 0, 0, width, height);
        batch.setColor(Color.WHITE);
        font.draw(batch, inventory1Title, itemsSelector.centerX - font.getBounds(inventory1Title).width/2, itemsSelector.centerY + 128);
        if (containerMode) {
            font.draw(batch, inventory2Title, containerItemsSelector.centerX - font.getBounds(inventory2Title).width/2, containerItemsSelector.centerY + 128);
        } else if (items.size() > 0) {
            Item currentItem = items.get(itemsSelector.getSelectedIndex());
            if (currentItem.sides == owner.headWear || currentItem.sides == owner.bodyWear) {
                if (language == 0) {
                    invenoryOptions.titles.set(1, "Take off");
                } else {
                    invenoryOptions.titles.set(1, "Снять");
                }
            } else if (currentItem.sides == owner.objectInHands) {
                if (language == 0) {
                    invenoryOptions.titles.set(1, "Unequip");
                } else {
                    invenoryOptions.titles.set(1, "Убрать из рук");
                }
            } else {
                if (currentItem.equipSlot == Item.EquipSlot.HEAD || currentItem.equipSlot == Item.EquipSlot.BODY) {
                    if (language == 0) {
                        invenoryOptions.titles.set(1, "Wear");
                    } else {
                        invenoryOptions.titles.set(1, "Надеть");
                    }
                } else {
                    if (language == 0) {
                        invenoryOptions.titles.set(1, "Equip");
                    } else {
                        invenoryOptions.titles.set(1, "Взять в руки");
                    }
                }
            }
        }
        otherOptions.draw(batch, paused);
        itemsSelector.draw(batch, paused);
        invenoryOptions.draw(batch, paused);
        if (containerMode) {
            containerItemsSelector.draw(batch, paused);
            containerOptions.draw(batch, paused);
        } else if (items.size() > 0) {
            Item currentItem = items.get(itemsSelector.getSelectedIndex());

            if (currentItem.bigIcon != null) {
                batch.draw(currentItem.bigIcon, width/4 - 256, 2 * height/3 - 226, 384, 384);
            }
            //String name = currentItem.namesInLanguages.get(language);
            String desc = currentItem.descriptionsInLanguages.get(language);
            //font.draw(batch, name, width/2 - font.getBounds(name).width/2, 2 * height/3 + 192);
            font.drawWrapped(batch, desc, 2*width/3, 2 * height/3, 300, BitmapFont.HAlignment.LEFT);
        }
        if (!otherOptions.enabled && otherIsSelected) {
            if (lastLeftSelected) {
                invenoryOptions.enabled = true;
                lastLeftSelected = true;
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                    invenoryOptions.setSelectedIndex(inventoryOpts.size()-1);
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                    invenoryOptions.setSelectedIndex(0);
                }
            } else {
                containerOptions.enabled = true;
                lastLeftSelected = false;
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                    containerOptions.setSelectedIndex(containerOpts.size()-1);
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                    containerOptions.setSelectedIndex(0);
                }
            }
            otherIsSelected = false;
        }
        String str = "<-  " + itemsSelector.curTitle;
        if (items.size() > 0 && items.get(itemsSelector.getSelectedIndex()).stack > 1) {
            str += " (" + items.get(itemsSelector.getSelectedIndex()).stack + ")";
        }
        invenoryOptions.titles.set(0, str + "  ->");

        if (containerMode) {
            String str2 = "<-  " + containerItemsSelector.curTitle;
            if (containerItems.size() > 0 && containerItems.get(containerItemsSelector.getSelectedIndex()).stack > 1) {
                str2 += " (" + containerItems.get(containerItemsSelector.getSelectedIndex()).stack + ")";
            }
            containerOptions.titles.set(0, str2 + "  ->");

            if (otherOptions.enabled) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    containerOptions.enabled = true;
                    lastLeftSelected = false;
                    otherIsSelected = false;
                    otherOptions.enabled = false;
                    containerOptions.setSelectedIndex(containerOpts.size()-1);
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                    invenoryOptions.enabled = true;
                    lastLeftSelected = true;
                    otherIsSelected = false;
                    otherOptions.enabled = false;
                    invenoryOptions.setSelectedIndex(inventoryOpts.size()-1);
                }

            } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                boolean changed = false;
                if (invenoryOptions.enabled && (invenoryOptions.getSelectedIndex() != 0 || items.size() < 2)) {
                    invenoryOptions.enabled = false;
                    containerOptions.enabled = true;
                    lastLeftSelected = false;
                    changed = true;
                } else if (containerOptions.enabled && (containerOptions.getSelectedIndex() != 0 || containerItems.size() < 2)) {
                    invenoryOptions.enabled = true;
                    containerOptions.enabled = false;
                    lastLeftSelected = true;
                    changed = true;
                }
                if (changed) {
                    if (invenoryOptions.enabled) {
                        invenoryOptions.setSelectedIndex(containerOptions.getSelectedIndex());
                    } else if (containerOptions.enabled) {
                        containerOptions.setSelectedIndex(invenoryOptions.getSelectedIndex());
                    }
                    releasedAfterChange = false;
                }
            }
            if (!Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                releasedAfterChange = true;
            }
            itemsSelector.enabled = invenoryOptions.enabled && invenoryOptions.getSelectedIndex() == 0 && releasedAfterChange;
            if (containerMode) {
                containerItemsSelector.enabled = containerOptions.enabled && containerOptions.getSelectedIndex() == 0 && releasedAfterChange;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (invenoryOptions.enabled && itemsSelector.getSelectedIndex() < items.size()) {
                    if (invenoryOptions.getSelectedIndex() == 1) {
                        int index = itemsSelector.getSelectedIndex();
                        Item item = items.get(itemsSelector.getSelectedIndex());
                        if (item.stack == 1) {
                            items.remove(index);
                            owner.updateFlagsAfterRemoval(item);
                        } else {
                            item.stack--;
                        }
                        if (item.sides == owner.headWear) {
                            owner.headWear = null;
                        } else if (item.sides == owner.bodyWear) {
                            owner.bodyWear = null;
                        } else if (item.sides == owner.objectInHands) {
                            owner.objectInHands = null;
                        }
                        addItem(containerItems, item);
                    } else if (invenoryOptions.getSelectedIndex() == 2) {
                        int offset = 0;
                        for (int i = 0; offset < items.size(); ++i) {
                            Item item = items.get(0 + offset);
                            for (int j = 0; j < item.stack; ++j) {
                                if (item.sides != owner.headWear && item.sides != owner.bodyWear && item.sides != owner.objectInHands) {
                                    items.remove(item);
                                    owner.updateFlagsAfterRemoval(item);
                                    addItem(containerItems, item);
                                } else {
                                    offset++;
                                }
                            }
                        }
                        //items.clear();
                    }
                    updateItems();
                } else if (containerOptions.enabled && containerItemsSelector.getSelectedIndex() < containerItems.size()) {
                    if (containerOptions.getSelectedIndex() == 1) {
                        int index = containerItemsSelector.getSelectedIndex();
                        Item item = containerItems.get(index);
                        if (item.stack == 1) {
                            containerItems.remove(index);
                        } else {
                            item.stack--;
                        }
                        addItem(items, item);
                    } else if (containerOptions.getSelectedIndex() == 2) {
                        for (int i = 0; i < containerItems.size(); ++i) {
                            for (int j = 0; j < containerItems.get(i).stack; ++j) {
                                Item item = containerItems.get(i);
                                addItem(items, item);
                            }
                        }
                        containerItems.clear();
                    }
                    updateItems();
                } else if (otherOptions.enabled && otherOptions.getSelectedIndex() == 0) {
                    closed = true;
                }
            }
            rightIsSelected = containerOptions.enabled;
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (invenoryOptions.enabled) {
                    if (invenoryOptions.getSelectedIndex() == 1) {
                        int index = itemsSelector.getSelectedIndex();
                        if (items.get(index).equipSlot == Item.EquipSlot.HEAD) {
                            if (owner.headWear != items.get(index).sides) {
                                owner.headWear = items.get(index).sides;
                            } else {
                                owner.headWear = null;
                            }
                        } else if (items.get(index).equipSlot == Item.EquipSlot.BODY) {
                            if (owner.bodyWear != items.get(index).sides) {
                                owner.bodyWear = items.get(index).sides;
                            } else {
                                owner.bodyWear = null;
                            }
                        } else {
                            if (owner.objectInHands != items.get(index).sides) {
                                owner.objectInHands = items.get(index).sides;
                            } else {
                                owner.objectInHands = null;
                            }
                        }
                        owner.setWears();
                    } else if (invenoryOptions.getSelectedIndex() == 2 && items.size() > 0) {
                        Item currentItem = items.get(itemsSelector.getSelectedIndex());

                        if (currentItem.equipSlot == Item.EquipSlot.HEAD) {
                            if (owner.headWear == currentItem.sides) {
                                owner.headWear = null;
                            }
                        } else if (currentItem.equipSlot == Item.EquipSlot.BODY) {
                            if (owner.bodyWear == currentItem.sides) {
                                owner.bodyWear = null;
                            }
                        } else {
                            if (owner.objectInHands == currentItem.sides) {
                                owner.objectInHands = null;
                            }
                        }

                        droppedItem = currentItem;
                        items.remove(itemsSelector.getSelectedIndex());
                        owner.updateFlagsAfterRemoval(droppedItem);
                        updateItems();
                    }
                }
                else if (otherOptions.enabled && otherOptions.getSelectedIndex() == 0) {
                    closed = true;
                }
            }
        }
        boolean leftIsSelected = invenoryOptions.enabled;
        if (!leftIsSelected && !rightIsSelected) {
            otherOptions.enabled = true;
            otherIsSelected = true;
        }

        if (!justOpened && Gdx.input.isKeyJustPressed(Input.Keys.I)/* || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)*/) {
            closed = true;
        }
        justOpened = false;
    }
}
