package com.itheima.activiti.dragram;

import org.activiti.bpmn.model.*;
import org.activiti.image.impl.DefaultProcessDiagramCanvas;
import org.springframework.beans.BeanUtils;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * 重写Activiti画图类
 * 用于生成自定义流程图
 */
public class ActivitiProcessDiagramCanvas extends DefaultProcessDiagramCanvas {

    public ActivitiProcessDiagramCanvas(int width, int height, int minX, int minY, String activityFontName, String labelFontName, String annotationFontName) {
        super(width, height, minX, minY, activityFontName, labelFontName, annotationFontName);

        Font font = new Font(activityFontName,
                Font.ITALIC,
                13);
        g.setFont(font);

        LABEL_FONT = new Font(labelFontName,
                Font.ITALIC,
                13);
    }

    static {
        // 高亮颜色
        HIGHLIGHT_COLOR = Color.decode("#67C23A");
        LABEL_COLOR = new Color(0, 0, 0);
        // 背景颜色
        TASK_BOX_COLOR = new Color(255, 255, 255);
        // 边框颜色
        TASK_BORDER_COLOR = Color.decode("#666666");
        EVENT_BORDER_COLOR = new Color(88, 88, 88);
        SUBPROCESS_BORDER_COLOR = new Color(0, 0, 0);
        ANNOTATION_FONT = null;
        THICK_TASK_BORDER_STROKE = new BasicStroke(2.0F);
        GATEWAY_TYPE_STROKE = new BasicStroke(3.0F);
        END_EVENT_STROKE = new BasicStroke(3.0F);
        MULTI_INSTANCE_STROKE = new BasicStroke(2.0F);
        EVENT_SUBPROCESS_STROKE = new BasicStroke(1.0F, 0, 0, 1.0F, new float[]{1.0F}, 0.0F);
        NON_INTERRUPTING_EVENT_STROKE = new BasicStroke(1.0F, 0, 0, 1.0F, new float[]{4.0F, 3.0F}, 0.0F);
        HIGHLIGHT_FLOW_STROKE = new BasicStroke(2.0F);
        ANNOTATION_STROKE = new BasicStroke(2.0F);
        ASSOCIATION_STROKE = new BasicStroke(2.0F, 0, 0, 1.0F, new float[]{2.0F, 2.0F}, 0.0F);
        ICON_PADDING = 5;


    }


    public void drawNoneEndEvent(String id,
                                 String name,
                                 GraphicInfo graphicInfo) {
        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();
        g.setPaint(EVENT_COLOR);
        Ellipse2D circle = new Ellipse2D.Double(graphicInfo.getX(),
                graphicInfo.getY(),
                graphicInfo.getWidth(),
                graphicInfo.getHeight());
        g.fill(circle);
        g.setPaint(EVENT_BORDER_COLOR);
        g.setStroke(END_EVENT_STROKE);
        g.draw(circle);
        g.setStroke(originalStroke);
        g.setPaint(originalPaint);

        // set element's id
        g.setCurrentGroupId(id);
        GraphicInfo graphicInfoLabel = new GraphicInfo();
        BeanUtils.copyProperties(graphicInfo, graphicInfoLabel);
        graphicInfoLabel.setY(graphicInfo.getY() + graphicInfo.getHeight() + 5);
        drawLabel(name,
                graphicInfoLabel);
    }

    public void drawHighLight(int x,
                              int y,
                              int width,
                              int height,
                              FlowNode flowNode) {
        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();

        g.setPaint(HIGHLIGHT_COLOR);
        g.setStroke(THICK_TASK_BORDER_STROKE);

        Shape circle;
        if (flowNode instanceof StartEvent) {
            circle = new Ellipse2D.Double(x, y, width, height);
        } else if (flowNode instanceof EndEvent) {
            circle = new Ellipse2D.Double(x, y, width, height);
        } else if (flowNode instanceof Gateway) {
            Polygon rhombus = new Polygon();
            rhombus.addPoint(x,
                    y + (height / 2));
            rhombus.addPoint(x + (width / 2),
                    y + height);
            rhombus.addPoint(x + width,
                    y + (height / 2));
            rhombus.addPoint(x + (width / 2),
                    y);
            circle = rhombus;
        } else {
            circle = new RoundRectangle2D.Double(x, y, width, height, 5, 5);
        }
        g.draw(circle);

        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }
}
