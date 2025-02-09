package com.horrorcore.engine.core.ui;

import org.joml.Vector4f;
import java.util.*;

public class LayoutManager {
    public static class LayoutArea {
        public float x, y, width, height;
        public float xPercent, yPercent, widthPercent, heightPercent;
        public Panel panel;
        public String area;

        public LayoutArea(String area, float xPercent, float yPercent,
                          float widthPercent, float heightPercent) {
            this.area = area;
            this.xPercent = xPercent;
            this.yPercent = yPercent;
            this.widthPercent = widthPercent;
            this.heightPercent = heightPercent;

            // Set initial dimensions to 0
            this.x = 0;
            this.y = 0;
            this.width = 0;
            this.height = 0;
        }

        public void updateDimensions(float totalWidth, float totalHeight) {
            x = xPercent * totalWidth;
            y = yPercent * totalHeight;
            width = widthPercent * totalWidth;
            height = heightPercent * totalHeight;

            // Debug output
            System.out.println(area + " area dimensions: x=" + x + ", y=" + y +
                    ", width=" + width + ", height=" + height);
        }
    }

    private float totalWidth;
    private float totalHeight;
    private final Map<String, LayoutArea> areas;
    private final List<Panel> panels;

    public LayoutManager() {
        this.areas = new HashMap<>();
        this.panels = new ArrayList<>();
        this.totalWidth = 0;
        this.totalHeight = 0;
    }

    public void setSize(float width, float height) {
        System.out.println("Setting layout size: " + width + "x" + height);
        this.totalWidth = width;
        this.totalHeight = height;
        updateLayout();
    }

    public void defineArea(String name, float xPercent, float yPercent,
                           float widthPercent, float heightPercent) {
        System.out.println("Defining area '" + name + "': x=" + xPercent +
                ", y=" + yPercent + ", width=" + widthPercent +
                ", height=" + heightPercent);

        LayoutArea area = new LayoutArea(name, xPercent, yPercent, widthPercent, heightPercent);
        areas.put(name, area);

        // If we already have dimensions, update the area immediately
        if (totalWidth > 0 && totalHeight > 0) {
            area.updateDimensions(totalWidth, totalHeight);
        }
    }

    public void addPanel(String areaName, Panel panel) {
        LayoutArea area = areas.get(areaName);
        if (area != null) {
            System.out.println("Adding panel to area '" + areaName + "'");
            area.panel = panel;
            panels.add(panel);

            // Update panel dimensions if we have a valid size
            if (totalWidth > 0 && totalHeight > 0) {
                area.updateDimensions(totalWidth, totalHeight);
                panel.setDimensions(area.x, area.y, area.width, area.height);
            }
        } else {
            System.out.println("Warning: No area found with name '" + areaName + "'");
        }
    }

    private void updateLayout() {
        if (totalWidth <= 0 || totalHeight <= 0) {
            System.out.println("Warning: Invalid dimensions - width: " + totalWidth +
                    ", height: " + totalHeight);
            return;
        }

        System.out.println("Updating layout with dimensions: " + totalWidth + "x" + totalHeight);

        // Update all area dimensions
        for (LayoutArea area : areas.values()) {
            area.updateDimensions(totalWidth, totalHeight);
            if (area.panel != null) {
                System.out.println("Updating panel in area '" + area.area +
                        "' to: x=" + area.x + ", y=" + area.y +
                        ", width=" + area.width + ", height=" + area.height);
                area.panel.setDimensions(area.x, area.y, area.width, area.height);
            }
        }
    }

    public void render() {
        for (Panel panel : panels) {
            panel.render();
        }
    }

    public void cleanup() {
        for (Panel panel : panels) {
            panel.cleanup();
        }
    }

    public Vector4f getAreaDimensions(String areaName) {
        LayoutArea area = areas.get(areaName);
        if (area != null) {
            return new Vector4f(area.x, area.y, area.width, area.height);
        }
        return null;
    }
}