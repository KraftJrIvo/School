package com.mygdx.schoolRPG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.schoolRPG.tools.AnimationSequence;
import com.mygdx.schoolRPG.tools.GlobalSequence;
import com.mygdx.schoolRPG.tools.MultiTile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectLoader {
    Area area;
    ArrayList<ArrayList<ArrayList<Integer>>> blocks;
    WorldObjectsHandler worldObjectsHandler;
    AssetManager assets;
    ArrayList<String> loadedParticlesNames;
    ArrayList<String> initializedParticlesNames;

    public ObjectLoader(WorldObjectsHandler worldObjectsHandler) {
        this.worldObjectsHandler = worldObjectsHandler;
        this.area = worldObjectsHandler.area;
        this.blocks = worldObjectsHandler.blocks;
        loadedParticlesNames = new ArrayList<String>();
        initializedParticlesNames = new ArrayList<String>();
    }

    public ObjectLoader() {
        loadedParticlesNames = new ArrayList<String>();
        initializedParticlesNames = new ArrayList<String>();
    }

    public void loadObjects(AssetManager assets, World world) {
        this.assets = assets;
        ArrayList<String> loadList = new ArrayList<String>();
        for (int k = 0; k < 4; ++k) {
            if (k == 2) continue;
            for (int i = 0; i < area.width; ++i) {
                for (int t = 0; t < area.height; ++t) {
                    int id = blocks.get(k).get(i).get(t);
                    if (id >= 0) {
                        if (world.names.get(id) == null) {
                            while (world.names.get(id) == null) id--;
                        }
                        String path = world.worldDir + "/" + world.names.get(id).replace("\\", "/") + ".png";
                        if (!loadList.contains(path) && !world.names.get(id).equals("")) {
                            assets.load(path, Texture.class);
                            if (k == 0) {
                                String stepSound = null;
                                if (id < world.stepSounds.size()) stepSound = world.stepSounds.get(id);
                                if (stepSound == null)
                                    stepSound = world.stepSounds.get(world.tileIndices.get(id));
                                //if (world.tileTypes.get(id) == 0) stepSound = world.stepSounds.get(id);
                                //else if (world.tileTypes.get(id) == 1) stepSound = world.stepSounds.get(world.spritesCount + world.tileIndices.get(id));
                                //else stepSound = world.stepSounds.get(world.spritesCount + world.tilesetsCount + world.tileIndices.get(id));
                                if (stepSound != null) {
                                    if (stepSound.contains(".")) {
                                        assets.load(world.worldDir + "/sounds/" + stepSound, Sound.class);
                                    } else {
                                        FileHandle soundDir = Gdx.files.internal(world.worldDir + "/sounds/" + stepSound );
                                        for (FileHandle entry: soundDir.list()) {
                                            assets.load(entry.path(), Sound.class);
                                        }
                                    }
                                }
                            }
                            loadList.add(path);
                        }
                    }
                }
            }
        }
        if (!area.ambient.equals("")) {
            assets.load(world.worldDir + "/sounds/" + area.ambient, Sound.class);
        }
        if (!world.platformMode) {
            loadChar(assets, world, 0);
        } else {
            loadParticle(world, "blood");
            loadParticle(world, "bone");
            loadParticle(world, "ribcage");
            loadParticle(world, "skull");
        }
        for (int i = 0; i < area.width; ++i) {
            for (int t = 0; t < area.height; ++t) {
                int angle = blocks.get(5).get(i).get(t);
                int objectCheckId = -1;
                if (angle < 0 || angle > 3) {
                    if (angle > 0) {
                        objectCheckId = angle - 100;
                    } else {
                        objectCheckId = angle + 156;
                    }
                }
                if (objectCheckId != -1) {
                    loadObject(assets, world, objectCheckId);
                } else if (blocks.get(4).get(i).get(t) != -1 && blocks.get(4).get(i).get(t) + 56 >= 1) {
                    int id = blocks.get(4).get(i).get(t) + 56;
                    if (angle == 0) {
                        loadChar(assets, world, id);
                    } else {
                        loadItem(assets, world, id);
                    }
                }
            }
        }
        loadBG(assets, world);
        loadWeather(assets, world);
    }

    private void loadChar(AssetManager assets, World world, int charId) {
        String path = world.worldDir + "/chars/" + charId;
        FileHandle fh = Gdx.files.internal(path + "/stats");
        if (fh.exists()) {
            FileHandle spriteFile = Gdx.files.internal(path + "/sprite.png");
            if (spriteFile.exists()) {
                assets.load(path + "/sprite.png", Texture.class);
            } else {
                assets.load(path + "/head.png", Texture.class);
                FileHandle bodyFile = Gdx.files.internal(path + "/body.png");
                if (bodyFile.exists()) {
                    assets.load(path + "/body.png", Texture.class);
                }
            }
            assets.load(path + "/speech.wav", Sound.class);
            ///FileHandle curDir1 = Gdx.files.internal(path + "/graphics");
            //if (curDir1.exists()) {
                //for (FileHandle entry1 : curDir1.list()) {
                    //assets.load(entry1.path(), Texture.class);
                //}
            //}
            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(fh.read()));
                String line = in.readLine();
                line = in.readLine();
                int varsCount = Integer.parseInt(line);
                for (int j = 0; j < varsCount; ++j) {
                    in.readLine();
                    in.readLine();
                }
                in.readLine();
                in.readLine();
                in.readLine();
                line = in.readLine();
                int count = Integer.parseInt(line);
                for (int j = 0; j < count; ++j) {
                    line = in.readLine();
                    loadItem(assets, world, line);
                    in.readLine();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (charId != 0) {
                FileHandle objectXML2 = Gdx.files.internal(path + "/dialog.xml");
                DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder2 = null;
                try {
                    dBuilder2 = dbFactory2.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
                org.w3c.dom.Document doc2 = null;
                try {
                    doc2 = dBuilder2.parse(objectXML2.read());
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                doc2.getDocumentElement().normalize();
                NodeList spritesList = doc2.getElementsByTagName("sprite");
                for (int jj = 0; jj < spritesList.getLength(); ++jj) {
                    Element element2 = (Element) spritesList.item(jj);
                    if (!element2.getAttribute("name").equals("")) {
                        assets.load(path + "/graphics/" + element2.getAttribute("name") + ".png", Texture.class);
                    }
                }
                NodeList itemList = doc2.getElementsByTagName("itemTransfer");
                for (int jj = 0; jj < itemList.getLength(); ++jj) {
                    Element element2 = (Element) itemList.item(jj);
                    if (!element2.getAttribute("itemName").equals("")) {
                        loadItem(assets, world, element2.getAttribute("itemName"));
                    }
                }
            }
        }
    }

    public void loadItem(AssetManager assets, World world, String itemName) {
        FileHandle path = Gdx.files.internal(world.worldDir + "/items/icons/" + itemName + ".png");
        assets.load(path.path(), Texture.class);
        path = Gdx.files.internal(world.worldDir + "/items/big_icons/" + itemName + ".png");
        if (path.exists()) {
            assets.load(path.path(), Texture.class);
        }
        path = Gdx.files.internal(world.worldDir + "/items/sides/" + itemName + ".png");
        if (path.exists()) {
            assets.load(path.path(), Texture.class);
        }
    }

    private void loadItem(AssetManager assets, World world, int itemId) {
        FileHandle itemDir =  Gdx.files.internal(world.folderPath + "/items");
        FileHandle itemXML = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document xml = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        boolean spawned = false;
        for (FileHandle entry: itemDir.list()) {
            if (entry.extension().equals("xml")) {
                itemXML = entry;
                try {
                    xml = dBuilder.parse(itemXML.read());
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                xml.getDocumentElement().normalize();
                int id = Integer.parseInt(xml.getDocumentElement().getAttribute("id"));
                if (id == itemId) {
                    loadItem(assets, world, entry.nameWithoutExtension());
                }
            }
            if (spawned) break;
        }
    }

    private void loadObject(AssetManager assets, World world, int objectCheckId) {
        FileHandle charDir =  Gdx.files.internal(world.worldDir + "/objects");
        FileHandle objectXML = null;
        for (FileHandle entry: charDir.list()) {
            if (entry.isDirectory()) continue;
            int id = Integer.parseInt(entry.nameWithoutExtension().split("_")[0]);
            if (id == objectCheckId) {
                objectXML = entry;
                break;
            }
        }
        if (objectXML != null) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            org.w3c.dom.Document doc = null;
            try {
                doc = dBuilder.parse(objectXML.read());
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            doc.getDocumentElement().normalize();

            NodeList stateList = doc.getElementsByTagName("state");
            for (int ii = 0; ii < stateList.getLength(); ++ii) {
                Element element = (Element) stateList.item(ii);
                if (element.getAttribute("hidePlayer").equals("true") && element.getAttribute("drawPlayer").equals("true")) {
                    assets.load(world.worldDir + "/objects/util/" + element.getAttribute("tex") + "_char.png", Texture.class);
                }
                if (!element.getAttribute("switchSound").equals("")) {
                    assets.load(world.worldDir + "/sounds/" + element.getAttribute("switchSound"), Sound.class);
                }
                if (!element.getAttribute("loopSound").equals("")) {
                    assets.load(world.worldDir + "/sounds/" + element.getAttribute("loopSound"), Sound.class);
                }
                if (element.getAttribute("teleportWorld").equals("") && !element.getAttribute("teleportRoom").equals("")) {
                    Area a = world.map.getAreaByName(element.getAttribute("teleportRoom"));
                    if (!a.loading) {
                        a.load();
                    }
                }
                if (!element.getAttribute("zLayerChange").equals("")) {
                    int zOff = Integer.parseInt(element.getAttribute("zLayerChange"));
                    if (area.z + zOff >= 0 && area.z + zOff < world.height) {
                        int aId = world.areaIds.get(area.x).get(area.y).get(area.z + zOff);
                        if (aId != -1) {
                            Area a = world.areas.get(aId);
                            if (!a.loading) {
                                a.load();
                            }
                        }
                    }
                }
                String path = null;
                if (element.getAttribute("texType").equals("anim")) {
                    path = world.worldDir + "/anim/" + element.getAttribute("tex") + ".png";
                } else if (!element.getAttribute("tex").equals("")) {
                    path = world.worldDir + "/" + element.getAttribute("tex") + ".png";
                }
                if (path != null) {
                    assets.load(path, Texture.class);
                }
                if (!element.getAttribute("initDialog").equals("")) {
                    FileHandle objectXML2 = Gdx.files.internal(world.worldDir + "/chars/0/dialog/" + element.getAttribute("initDialog") + ".xml");
                    DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder2 = null;
                    try {
                        dBuilder2 = dbFactory2.newDocumentBuilder();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                    org.w3c.dom.Document doc2 = null;
                    try {
                        doc2 = dBuilder2.parse(objectXML2.read());
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    doc2.getDocumentElement().normalize();
                    NodeList spritesList = doc2.getElementsByTagName("sprite");
                    for (int jj = 0; jj < spritesList.getLength(); ++jj) {
                        Element element2 = (Element) spritesList.item(jj);
                        if (!element2.getAttribute("name").equals("")) {
                            assets.load(world.worldDir + "/chars/0/graphics/" + element2.getAttribute("name") + ".png", Texture.class);
                        }
                    }
                    NodeList itemList = doc2.getElementsByTagName("itemTransfer");
                    for (int jj = 0; jj < itemList.getLength(); ++jj) {
                        Element element2 = (Element) itemList.item(jj);
                        if (!element2.getAttribute("itemName").equals("")) {
                            loadItem(assets, world, element2.getAttribute("itemName"));
                        }
                    }
                }

                NodeList prtList = element.getElementsByTagName("particle");
                for (int jj = 0; jj < prtList.getLength(); ++jj) {
                    Element element2 = (Element) prtList.item(jj);
                    loadParticles(world, element2);
                }
                prtList = element.getElementsByTagName("jumpAsPrt");
                for (int jj = 0; jj < prtList.getLength(); ++jj) {
                    Element element2 = (Element) prtList.item(jj);
                    loadParticles(world, element2);
                }

            }
            NodeList itemList = doc.getElementsByTagName("item");
            for (int jj = 0; jj < itemList.getLength(); ++jj) {
                Element element2 = (Element) itemList.item(jj);
                loadItem(assets, world, element2.getAttribute("name"));
            }
        }
    }

    private void loadParticle(World world, String name) {
        FileHandle particlesDir = Gdx.files.internal(world.worldDir + "/particles/" + name);
        for (FileHandle entry: particlesDir.list()) {
            if (entry.path().endsWith(".png")) {
                assets.load(entry.path(), Texture.class);
            }
        }
        FileHandle objectXML2 = Gdx.files.internal(world.worldDir + "/particles/" + name + "/stats.xml");
        DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder2 = null;
        try {
            dBuilder2 = dbFactory2.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Document doc2 = null;
        try {
            doc2 = dBuilder2.parse(objectXML2.read());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc2.getDocumentElement().normalize();
        NodeList stateList = doc2.getElementsByTagName("state");
        for (int ii = 0; ii < stateList.getLength(); ++ii) {
            Element element2 = (Element) stateList.item(ii);
            if (!element2.getAttribute("flySoundLoop").equals("")) {
                assets.load(world.worldDir + "/sounds/" + element2.getAttribute("flySoundLoop"), Sound.class);
            }
            if (!element2.getAttribute("bounceSound").equals("")) {
                assets.load(world.worldDir + "/sounds/" + element2.getAttribute("bounceSound"), Sound.class);
            }
        }
        NodeList prtList = doc2.getElementsByTagName("child-particle");
        loadedParticlesNames.add(name);
        for (int ii = 0; ii < prtList.getLength(); ++ii) {
            Element element2 = (Element) prtList.item(ii);
            loadParticles(world, element2);
        }
    }

    private void loadParticles(World world, Element element) {
        if (loadedParticlesNames.contains(element.getAttribute("name"))) return;
        String soundPath = element.getAttribute("spawnSound");
        if (!soundPath.equals("")) assets.load(world.worldDir + "/sounds/" + soundPath, Sound.class);

        loadParticle(world, element.getAttribute("name"));
    }

    public void initializeObjects(AssetManager assets, World world) {
        for (int k = 0; k < 4; ++k) {
            if (k == 2) continue;
            for (int i = 0; i < area.width; ++i) {
                for (int t = 0; t < area.height; ++t) {
                    int id = blocks.get(k).get(i).get(t);
                    if (id >= 0) {
                        if (world.names.get(id) == null) {
                            while (world.names.get(id) == null) id--;
                        }
                        String path = world.worldDir + "/" + world.names.get(id) + ".png";
                        int type = world.tileTypes.get(id);
                        int index = world.tileIndices.get(id);
                        if (!world.names.get(id).equals("")) {
                            if (type == 0 && world.sprites.size() > index) {
                                world.sprites.set(index, new Texture(path));
                            } else if (type == 1) {
                                if (path.contains("tileset") && world.tilesets.size() > index) {
                                    Pattern p = Pattern.compile("(\\d+)x(\\d+)", Pattern.CASE_INSENSITIVE);
                                    Matcher m = p.matcher(world.names.get(id));
                                    m.find();
                                    world.tilesets.set(index, new MultiTile(assets.get(path.replace("\\", "/"), Texture.class), Integer.parseInt(m.group(1)),  Integer.parseInt(m.group(2))));
                                } else {
                                    world.tiles.set(index, new BlockMultiTile(assets.get(path.replace("\\", "/"), Texture.class)));
                                }
                            } else if (world.animations.size() > index) {
                                world.animations.set(index, new AnimationSequence(assets, path.replace("\\", "/"), 12, true));
                            }
                        }
                    } else if (blocks.get(4).get(i).get(t) != -1 && blocks.get(4).get(i).get(t) + 56 >= 1) {
                        int idd = blocks.get(4).get(i).get(t) + 56;
                        FileHandle fh = Gdx.files.internal(world.worldDir + "/chars/" + idd + "/stats");
                        if (fh.exists()) {
                            if (blocks.get(5).get(i).get(t) == 0) {
                                if (assets.isLoaded(world.worldDir + "/chars/" + idd + "/sprite.png")) {
                                    world.characterMaker.sprites.set(idd, assets.get(world.worldDir + "/chars/" + idd + "/sprite.png", Texture.class));
                                } else {
                                    world.characterMaker.heads.set(idd, new GlobalSequence(assets, world.worldDir + "/chars/" + idd + "/head.png", 3));
                                    if (assets.isLoaded(world.worldDir + "/chars/" + idd + "/body.png")) {
                                        world.characterMaker.bodies.set(idd, new GlobalSequence(assets, world.worldDir + "/chars/" + idd + "/body.png", 3));
                                    } else {
                                        world.characterMaker.bodies.set(idd, new GlobalSequence(assets,"char/body_male.png", 3));
                                    }
                                }
                            } else {

                            }
                        }
                    }
                }
            }
        }
        if (!world.platformMode && world.characterMaker.heads.get(0) == null && world.characterMaker.sprites.get(0) == null) {
            if (assets.isLoaded(world.worldDir + "/chars/0/sprite.png")) {
                world.characterMaker.sprites.set(0, assets.get(world.worldDir + "/chars/0/sprite.png", Texture.class));
            } else {
                world.characterMaker.heads.set(0, new GlobalSequence(assets, world.worldDir + "/chars/0/head.png", 3));
                if (assets.isLoaded(world.worldDir + "/chars/0/body.png")) {
                    world.characterMaker.bodies.set(0, new GlobalSequence(assets, world.worldDir + "/chars/0/body.png", 3));
                } else {
                    world.characterMaker.bodies.set(0, new GlobalSequence(assets,"char/body_male.png", 3));
                }
            }
        }
        for (int i = initializedParticlesNames.size(); i < loadedParticlesNames.size(); ++i) {
            world.particles.set(world.particlesPaths.indexOf(loadedParticlesNames.get(i)), new ParticleProperties(world, assets, world.worldDir + "/particles/" + loadedParticlesNames.get(i) + "/stats.xml", world.menu));
            initializedParticlesNames.add(loadedParticlesNames.get(i));
        }

    }

    private void loadBG(AssetManager assets, World world)
    {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(Gdx.files.internal(world.worldDir.path() + "/bg/bg").read()));
            String line = "_";
            boolean defaultBG = true;
            while (line != null) {
                if (line.equals(area.name)) {
                    defaultBG = false;
                }
                line = in.readLine();
            }
            in = new BufferedReader(new InputStreamReader(Gdx.files.internal(world.worldDir.path() + "/bg/bg").read()));
            int numLayers = Integer.parseInt(in.readLine());

            for (int i = 0; i < numLayers; ++i) {
                line = in.readLine();
                String vals[] = line.split(" ");
                if (defaultBG) {
                    assets.load(world.worldDir.path() + "/bg/" + vals[0] + ".png", Texture.class);
                }
            }
            if (!defaultBG) {
                int numCustomBGGroups = Integer.parseInt(in.readLine());
                for (int z = 0; z < numCustomBGGroups; ++z) {
                    int numCustomBGs = Integer.parseInt(in.readLine());
                    ArrayList<Area> customAreas = new ArrayList<Area>();
                    boolean thiss = false;
                    for (int i = 0; i < numCustomBGs; ++i) {
                        line = in.readLine();
                        if (line.equals(area.name)) thiss = true;
                        customAreas.add(world.map.getAreaByName(line));
                    }
                    numLayers = Integer.parseInt(in.readLine());
                    for (int j = 0; j < numLayers; ++j) {
                        line = in.readLine();
                        String vals[] = line.split(" ");
                        if (thiss) {
                            assets.load(world.worldDir.path() + "/bg/" + vals[0] + ".png", Texture.class);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWeather(AssetManager assets, World world) {
        FileHandle objectXML2 = Gdx.files.internal(world.worldDir + "/weather.xml");
        if (!objectXML2.exists()) return;
        DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder2 = null;
        try {
            dBuilder2 = dbFactory2.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Document doc2 = null;
        try {
            doc2 = dBuilder2.parse(objectXML2.read());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc2.getDocumentElement().normalize();
        NodeList prtList = doc2.getElementsByTagName("particleSpawn");
        for (int ii = 0; ii < prtList.getLength(); ++ii) {
            Element element2 = (Element) prtList.item(ii);
            loadParticles(world, element2);
        }
    }

}
