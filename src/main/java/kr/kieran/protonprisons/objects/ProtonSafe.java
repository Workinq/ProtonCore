/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.objects;

import kr.kieran.protonprisons.utilities.SerializationUtil;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ProtonSafe
{

    private final int id;
    public int getId() { return id; }

    private final String name;
    public String getName() { return name; }

    private final List<String> contents;
    public List<String> getContents() { return Collections.unmodifiableList(contents); }
    public void addItem(ItemStack item) { this.addItem(SerializationUtil.serializeItem(item)); }
    public void addItem(String serializedItem) { this.contents.add(serializedItem); }
    public void removeItem(ItemStack item) { this.removeItem(SerializationUtil.serializeItem(item)); }
    public void removeItem(String serializedItem) { this.contents.remove(serializedItem); }

    public ProtonSafe(int id, String name, List<String> contents)
    {
        this.id = id;
        this.name = name;
        this.contents = contents;
    }

}
