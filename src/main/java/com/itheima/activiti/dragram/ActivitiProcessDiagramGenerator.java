package com.itheima.activiti.dragram;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.exception.ActivitiInterchangeInfoNotFoundException;
import org.activiti.image.impl.DefaultProcessDiagramCanvas;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 重写Activiti画图类
 * 用于生成自定义流程图
 */
@Slf4j
public class ActivitiProcessDiagramGenerator extends DefaultProcessDiagramGenerator implements ProcessDiagramGenerator {

    private static Double MIN_WIDTH = 1100.0;

    protected Map<Class<? extends BaseElement>, ActivityDrawInstruction> activityDrawInstructions = new HashMap<Class<? extends BaseElement>, ActivityDrawInstruction>();

    protected Map<Class<? extends BaseElement>, ArtifactDrawInstruction> artifactDrawInstructions = new HashMap<Class<? extends BaseElement>, ArtifactDrawInstruction>();

    public ActivitiProcessDiagramGenerator() {
        super();
        // start event
        activityDrawInstructions.put(StartEvent.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        StartEvent startEvent = (StartEvent) flowNode;
                        if (startEvent.getEventDefinitions() != null && !startEvent.getEventDefinitions().isEmpty()) {
                            EventDefinition eventDefinition = startEvent.getEventDefinitions().get(0);
                            if (eventDefinition instanceof TimerEventDefinition) {
                                processDiagramCanvas.drawTimerStartEvent(flowNode.getId(),
                                        graphicInfo);
                            } else if (eventDefinition instanceof ErrorEventDefinition) {
                                processDiagramCanvas.drawErrorStartEvent(flowNode.getId(),
                                        graphicInfo);
                            } else if (eventDefinition instanceof SignalEventDefinition) {
                                processDiagramCanvas.drawSignalStartEvent(flowNode.getId(),
                                        graphicInfo);
                            } else if (eventDefinition instanceof MessageEventDefinition) {
                                processDiagramCanvas.drawMessageStartEvent(flowNode.getId(),
                                        graphicInfo);
                            } else {
                                processDiagramCanvas.drawNoneStartEvent(flowNode.getId(),
                                        graphicInfo);
                            }
                        } else {
                            processDiagramCanvas.drawNoneStartEvent(flowNode.getId(),
                                    graphicInfo);
                        }
                    }
                });

        // signal catch
        activityDrawInstructions.put(IntermediateCatchEvent.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        IntermediateCatchEvent intermediateCatchEvent = (IntermediateCatchEvent) flowNode;
                        if (intermediateCatchEvent.getEventDefinitions() != null && !intermediateCatchEvent.getEventDefinitions()
                                .isEmpty()) {
                            if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                                processDiagramCanvas.drawCatchingSignalEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        true);
                            } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {
                                processDiagramCanvas.drawCatchingTimerEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        true);
                            } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                                processDiagramCanvas.drawCatchingMessageEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        true);
                            }
                        }
                    }
                });

        // signal throw
        activityDrawInstructions.put(ThrowEvent.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        ThrowEvent throwEvent = (ThrowEvent) flowNode;
                        if (throwEvent.getEventDefinitions() != null && !throwEvent.getEventDefinitions().isEmpty()) {
                            if (throwEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                                processDiagramCanvas.drawThrowingSignalEvent(flowNode.getId(),
                                        graphicInfo);
                            } else if (throwEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                                processDiagramCanvas.drawThrowingCompensateEvent(flowNode.getId(),
                                        graphicInfo);
                            } else {
                                processDiagramCanvas.drawThrowingNoneEvent(flowNode.getId(),
                                        graphicInfo);
                            }
                        } else {
                            processDiagramCanvas.drawThrowingNoneEvent(flowNode.getId(),
                                    graphicInfo);
                        }
                    }
                });

        // end event

        activityDrawInstructions.put(EndEvent.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        EndEvent endEvent = (EndEvent) flowNode;
                        if (endEvent.getEventDefinitions() != null && !endEvent.getEventDefinitions().isEmpty()) {
                            if (endEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {
                                processDiagramCanvas.drawErrorEndEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo);
                            } else {
                                processDiagramCanvas.drawNoneEndEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo);
                            }
                        } else {
                            processDiagramCanvas.drawNoneEndEvent(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo);
                        }
                    }
                });

        // task
        activityDrawInstructions.put(Task.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // user task
        activityDrawInstructions.put(UserTask.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawUserTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // script task
        activityDrawInstructions.put(ScriptTask.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawScriptTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // service task
        activityDrawInstructions.put(ServiceTask.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        ServiceTask serviceTask = (ServiceTask) flowNode;
                        processDiagramCanvas.drawServiceTask(flowNode.getId(),
                                serviceTask.getName(),
                                graphicInfo);
                    }
                });

        // receive task
        activityDrawInstructions.put(ReceiveTask.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawReceiveTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // send task
        activityDrawInstructions.put(SendTask.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawSendTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // manual task
        activityDrawInstructions.put(ManualTask.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawManualTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // businessRuleTask task
        activityDrawInstructions.put(BusinessRuleTask.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawBusinessRuleTask(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // exclusive gateway
        activityDrawInstructions.put(ExclusiveGateway.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawExclusiveGateway(flowNode.getId(),
                                graphicInfo);
                    }
                });

        // inclusive gateway
        activityDrawInstructions.put(InclusiveGateway.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawInclusiveGateway(flowNode.getId(),
                                graphicInfo);
                    }
                });

        // parallel gateway
        activityDrawInstructions.put(ParallelGateway.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawParallelGateway(flowNode.getId(),
                                graphicInfo);
                    }
                });

        // event based gateway
        activityDrawInstructions.put(EventGateway.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawEventBasedGateway(flowNode.getId(),
                                graphicInfo);
                    }
                });

        // Boundary timer
        activityDrawInstructions.put(BoundaryEvent.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        BoundaryEvent boundaryEvent = (BoundaryEvent) flowNode;
                        if (boundaryEvent.getEventDefinitions() != null && !boundaryEvent.getEventDefinitions().isEmpty()) {
                            if (boundaryEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {

                                processDiagramCanvas.drawCatchingTimerEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            } else if (boundaryEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {

                                processDiagramCanvas.drawCatchingErrorEvent(flowNode.getId(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            } else if (boundaryEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                                processDiagramCanvas.drawCatchingSignalEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            } else if (boundaryEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                                processDiagramCanvas.drawCatchingMessageEvent(flowNode.getId(),
                                        flowNode.getName(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            } else if (boundaryEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                                processDiagramCanvas.drawCatchingCompensateEvent(flowNode.getId(),
                                        graphicInfo,
                                        boundaryEvent.isCancelActivity());
                            }
                        }
                    }
                });

        // subprocess
        activityDrawInstructions.put(SubProcess.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                            processDiagramCanvas.drawCollapsedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    false);
                        } else {
                            processDiagramCanvas.drawExpandedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    SubProcess.class);
                        }
                    }
                });
        // transaction
        activityDrawInstructions.put(Transaction.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                            processDiagramCanvas.drawCollapsedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    false);
                        } else {
                            processDiagramCanvas.drawExpandedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    Transaction.class);
                        }
                    }
                });

        // Event subprocess
        activityDrawInstructions.put(EventSubProcess.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                            processDiagramCanvas.drawCollapsedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    true);
                        } else {
                            processDiagramCanvas.drawExpandedSubProcess(flowNode.getId(),
                                    flowNode.getName(),
                                    graphicInfo,
                                    EventSubProcess.class);
                        }
                    }
                });

        // call activity
        activityDrawInstructions.put(CallActivity.class,
                new ActivityDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     FlowNode flowNode) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                        processDiagramCanvas.drawCollapsedCallActivity(flowNode.getId(),
                                flowNode.getName(),
                                graphicInfo);
                    }
                });

        // text annotation
        artifactDrawInstructions.put(TextAnnotation.class,
                new ArtifactDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     Artifact artifact) {
                        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(artifact.getId());
                        TextAnnotation textAnnotation = (TextAnnotation) artifact;
                        processDiagramCanvas.drawTextAnnotation(textAnnotation.getId(),
                                textAnnotation.getText(),
                                graphicInfo);
                    }
                });

        // association
        artifactDrawInstructions.put(Association.class,
                new ArtifactDrawInstruction() {

                    @Override
                    public void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                                     BpmnModel bpmnModel,
                                     Artifact artifact) {
                        Association association = (Association) artifact;
                        String sourceRef = association.getSourceRef();
                        String targetRef = association.getTargetRef();

                        // source and target can be instance of FlowElement or Artifact
                        BaseElement sourceElement = bpmnModel.getFlowElement(sourceRef);
                        BaseElement targetElement = bpmnModel.getFlowElement(targetRef);
                        if (sourceElement == null) {
                            sourceElement = bpmnModel.getArtifact(sourceRef);
                        }
                        if (targetElement == null) {
                            targetElement = bpmnModel.getArtifact(targetRef);
                        }
                        List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
                        graphicInfoList = connectionPerfectionizer(processDiagramCanvas,
                                bpmnModel,
                                sourceElement,
                                targetElement,
                                graphicInfoList);
                        int xPoints[] = new int[graphicInfoList.size()];
                        int yPoints[] = new int[graphicInfoList.size()];
                        for (int i = 1; i < graphicInfoList.size(); i++) {
                            GraphicInfo graphicInfo = graphicInfoList.get(i);
                            GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

                            if (i == 1) {
                                xPoints[0] = (int) previousGraphicInfo.getX();
                                yPoints[0] = (int) previousGraphicInfo.getY();
                            }
                            xPoints[i] = (int) graphicInfo.getX();
                            yPoints[i] = (int) graphicInfo.getY();
                        }

                        AssociationDirection associationDirection = association.getAssociationDirection();
                        processDiagramCanvas.drawAssociation(xPoints,
                                yPoints,
                                associationDirection,
                                false);
                    }
                });
    }

    public InputStream generateDiagram(BpmnModel bpmnModel, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName, boolean generateDefaultDiagram, String defaultDiagramImageFileName) {
        if (!bpmnModel.hasDiagramInterchangeInfo()) {
            if (!generateDefaultDiagram) {
                throw new ActivitiInterchangeInfoNotFoundException("No interchange information found.");
            } else {
                return this.getDefaultDiagram(defaultDiagramImageFileName);
            }
        } else {
            return this.generateProcessDiagram(bpmnModel, highLightedActivities, highLightedFlows, activityFontName, labelFontName, annotationFontName).generateImage();
        }
    }

    protected DefaultProcessDiagramCanvas generateProcessDiagram(BpmnModel bpmnModel, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName) {
        this.prepareBpmnModel(bpmnModel);

        parseBpmnLocationMap(bpmnModel);

        ActivitiProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(bpmnModel, activityFontName, labelFontName, annotationFontName);
        Iterator var8 = bpmnModel.getPools().iterator();

        while (var8.hasNext()) {
            Pool pool = (Pool) var8.next();
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            processDiagramCanvas.drawPoolOrLane(pool.getId(), pool.getName(), graphicInfo);
        }

        var8 = bpmnModel.getProcesses().iterator();

        Process process;
        Iterator var16;
        while (var8.hasNext()) {
            process = (Process) var8.next();
            var16 = process.getLanes().iterator();

            while (var16.hasNext()) {
                Lane lane = (Lane) var16.next();
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(lane.getId());
                processDiagramCanvas.drawPoolOrLane(lane.getId(), lane.getName(), graphicInfo);
            }
        }

        var8 = bpmnModel.getProcesses().iterator();

        while (var8.hasNext()) {
            process = (Process) var8.next();
            var16 = process.findFlowElementsOfType(FlowNode.class).iterator();

            while (var16.hasNext()) {
                FlowNode flowNode = (FlowNode) var16.next();
                this.drawActivity(processDiagramCanvas, bpmnModel, flowNode, highLightedActivities, highLightedFlows);
            }
        }

        var8 = bpmnModel.getProcesses().iterator();

        while (true) {
            List subProcesses;
            do {
                if (!var8.hasNext()) {
                    return processDiagramCanvas;
                }

                process = (Process) var8.next();
                var16 = process.getArtifacts().iterator();

                while (var16.hasNext()) {
                    Artifact artifact = (Artifact) var16.next();
                    this.drawArtifact(processDiagramCanvas, bpmnModel, artifact);
                }

                subProcesses = process.findFlowElementsOfType(SubProcess.class, true);
            } while (subProcesses == null);

            Iterator var20 = subProcesses.iterator();

            while (var20.hasNext()) {
                SubProcess subProcess = (SubProcess) var20.next();
                Iterator var13 = subProcess.getArtifacts().iterator();

                while (var13.hasNext()) {
                    Artifact subProcessArtifact = (Artifact) var13.next();
                    this.drawArtifact(processDiagramCanvas, bpmnModel, subProcessArtifact);
                }
            }
        }
    }

    /**
     * 自定义方法
     * 转换 bpmn模型的位置坐标 整体前移
     *
     * @param bpmnModel
     */
    private void parseBpmnLocationMap(BpmnModel bpmnModel) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        for (String s : bpmnModel.getLocationMap().keySet()) {
            GraphicInfo graphicInfo = bpmnModel.getLocationMap().get(s);
            if (minX > graphicInfo.getX()) {
                minX = graphicInfo.getX();
            }
            if (minY > graphicInfo.getY()) {
                minY = graphicInfo.getY();
            }

            if (maxX < graphicInfo.getX() + graphicInfo.getWidth()) {
                maxX = graphicInfo.getX() + graphicInfo.getWidth();
            }
        }
        for (String s : bpmnModel.getLabelLocationMap().keySet()) {
            GraphicInfo graphicInfo = bpmnModel.getLabelLocationMap().get(s);
            if (minX > graphicInfo.getX()) {
                minX = graphicInfo.getX();
            }
            if (minY > graphicInfo.getY()) {
                minY = graphicInfo.getY();
            }

            if (maxX < graphicInfo.getX() + graphicInfo.getWidth()) {
                maxX = graphicInfo.getX() + graphicInfo.getWidth();
            }
        }
        for (String s : bpmnModel.getFlowLocationMap().keySet()) {
            for (GraphicInfo graphicInfo : bpmnModel.getFlowLocationMap().get(s)) {
                if (minX > graphicInfo.getX()) {
                    minX = graphicInfo.getX();
                }
                if (minY > graphicInfo.getY()) {
                    minY = graphicInfo.getY();
                }

                if (maxX < graphicInfo.getX() + graphicInfo.getWidth()) {
                    maxX = graphicInfo.getX() + graphicInfo.getWidth();
                }
            }
        }
        if (maxX - minX + 10 < MIN_WIDTH) {
            double difference = MIN_WIDTH - (maxX - minX);
            double leftBlank = difference / 2;
            minX = minX - leftBlank;
        }
        for (String s : bpmnModel.getLocationMap().keySet()) {
            bpmnModel.getLocationMap().get(s).setX(bpmnModel.getLocationMap().get(s).getX() - minX + 10);
            bpmnModel.getLocationMap().get(s).setY(bpmnModel.getLocationMap().get(s).getY() - minY + 10);
        }
        for (String s : bpmnModel.getLabelLocationMap().keySet()) {
            bpmnModel.getLabelLocationMap().get(s).setX(bpmnModel.getLabelLocationMap().get(s).getX() - minX + 10);
            bpmnModel.getLabelLocationMap().get(s).setY(bpmnModel.getLabelLocationMap().get(s).getY() - minY + 10);
        }

        for (String s : bpmnModel.getFlowLocationMap().keySet()) {
            for (GraphicInfo graphicInfo : bpmnModel.getFlowLocationMap().get(s)) {
                graphicInfo.setX(graphicInfo.getX() - minX + 10);
                graphicInfo.setY(graphicInfo.getY() - minY + 10);
            }
        }
    }

    protected static ActivitiProcessDiagramCanvas initProcessDiagramCanvas(BpmnModel bpmnModel, String activityFontName, String labelFontName, String annotationFontName) {
        double minX = 1.7976931348623157E308D;
        double maxX = 0.0D;
        double minY = 1.7976931348623157E308D;
        double maxY = 0.0D;

        GraphicInfo graphicInfo;
        for (Iterator var12 = bpmnModel.getPools().iterator(); var12.hasNext(); maxY = graphicInfo.getY() + graphicInfo.getHeight()) {
            Pool pool = (Pool) var12.next();
            graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            minX = graphicInfo.getX();
            maxX = graphicInfo.getX() + graphicInfo.getWidth();
            minY = graphicInfo.getY();
        }

        List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);
        Iterator var22 = flowNodes.iterator();

        label163:
        while (true) {
            GraphicInfo flowNodeGraphicInfo;
            FlowNode flowNode;
            do {
                if (!var22.hasNext()) {
                    List<Artifact> artifacts = gatherAllArtifacts(bpmnModel);
                    Iterator var25 = artifacts.iterator();

                    while (var25.hasNext()) {
                        Artifact artifact = (Artifact) var25.next();
                        GraphicInfo artifactGraphicInfo = bpmnModel.getGraphicInfo(artifact.getId());
                        if (artifactGraphicInfo != null) {
                            if (artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth() > maxX) {
                                maxX = artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth();
                            }

                            if (artifactGraphicInfo.getX() < minX) {
                                minX = artifactGraphicInfo.getX();
                            }

                            if (artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight() > maxY) {
                                maxY = artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight();
                            }

                            if (artifactGraphicInfo.getY() < minY) {
                                minY = artifactGraphicInfo.getY();
                            }
                        }

                        List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
                        if (graphicInfoList != null) {
                            Iterator var33 = graphicInfoList.iterator();

                            while (var33.hasNext()) {
                                graphicInfo = (GraphicInfo) var33.next();
                                if (graphicInfo.getX() > maxX) {
                                    maxX = graphicInfo.getX();
                                }

                                if (graphicInfo.getX() < minX) {
                                    minX = graphicInfo.getX();
                                }

                                if (graphicInfo.getY() > maxY) {
                                    maxY = graphicInfo.getY();
                                }

                                if (graphicInfo.getY() < minY) {
                                    minY = graphicInfo.getY();
                                }
                            }
                        }
                    }

                    int nrOfLanes = 0;
                    Iterator var28 = bpmnModel.getProcesses().iterator();

                    while (var28.hasNext()) {
                        Process process = (Process) var28.next();
                        Iterator var32 = process.getLanes().iterator();

                        while (var32.hasNext()) {
                            Lane l = (Lane) var32.next();
                            ++nrOfLanes;
                            graphicInfo = bpmnModel.getGraphicInfo(l.getId());
                            if (graphicInfo != null) {
                                if (graphicInfo.getX() + graphicInfo.getWidth() > maxX) {
                                    maxX = graphicInfo.getX() + graphicInfo.getWidth();
                                }

                                if (graphicInfo.getX() < minX) {
                                    minX = graphicInfo.getX();
                                }

                                if (graphicInfo.getY() + graphicInfo.getHeight() > maxY) {
                                    maxY = graphicInfo.getY() + graphicInfo.getHeight();
                                }

                                if (graphicInfo.getY() < minY) {
                                    minY = graphicInfo.getY();
                                }
                            }
                        }
                    }

                    if (flowNodes.isEmpty() && bpmnModel.getPools().isEmpty() && nrOfLanes == 0) {
                        minX = 0.0D;
                        minY = 0.0D;
                    }
                    // 固定最小宽度
                    double width = maxX + 10;
                    if (width < MIN_WIDTH) {
                        width = MIN_WIDTH;
                    }
                    return new ActivitiProcessDiagramCanvas((int) width, (int) maxY + 50, (int) minX, (int) minY, "宋体", "宋体", "宋体");
                }

                flowNode = (FlowNode) var22.next();
                flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            } while (flowNodeGraphicInfo == null);

            if (flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth() > maxX) {
                maxX = flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth();
            }

            if (flowNodeGraphicInfo.getX() < minX) {
                minX = flowNodeGraphicInfo.getX();
            }

            if (flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight() > maxY) {
                maxY = flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight();
            }

            if (flowNodeGraphicInfo.getY() < minY) {
                minY = flowNodeGraphicInfo.getY();
            }

            Iterator var16 = flowNode.getOutgoingFlows().iterator();

            while (true) {
                List graphicInfoList;
                do {
                    if (!var16.hasNext()) {
                        continue label163;
                    }

                    SequenceFlow sequenceFlow = (SequenceFlow) var16.next();
                    graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
                } while (graphicInfoList == null);

                Iterator var19 = graphicInfoList.iterator();

                while (var19.hasNext()) {
                    graphicInfo = (GraphicInfo) var19.next();
                    if (graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }

                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }

                    if (graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }

                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }
    }


    /**
     * 重写 解决线条不显示文字
     *
     * @param processDiagramCanvas
     * @param bpmnModel
     * @param flowNode
     * @param highLightedActivities
     * @param highLightedFlows
     */
    protected void drawActivity(ActivitiProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode, List<String> highLightedActivities, List<String> highLightedFlows) {
        ActivityDrawInstruction drawInstruction = (ActivityDrawInstruction) this.activityDrawInstructions.get(flowNode.getClass());
        boolean highLighted;
        if (drawInstruction != null) {
            try {
                drawInstruction.draw(processDiagramCanvas, bpmnModel, flowNode);
                boolean multiInstanceSequential = false;
                boolean multiInstanceParallel = false;
                highLighted = false;
                if (flowNode instanceof Activity) {
                    Activity activity = (Activity) flowNode;
                    MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = activity.getLoopCharacteristics();
                    if (multiInstanceLoopCharacteristics != null) {
                        multiInstanceSequential = multiInstanceLoopCharacteristics.isSequential();
                        multiInstanceParallel = !multiInstanceSequential;
                    }
                }

                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                if (!(flowNode instanceof SubProcess)) {
                    if (flowNode instanceof CallActivity) {
                        highLighted = true;
                    }
                } else {
                    highLighted = graphicInfo.getExpanded() != null && !graphicInfo.getExpanded();
                }

                processDiagramCanvas.drawActivityMarkers((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight(), multiInstanceSequential, multiInstanceParallel, highLighted);
                if (highLightedActivities.contains(flowNode.getId())) {
                    drawHighLight(processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()), flowNode);
                }
            } catch (Exception e) {
                log.info("Exception ：{}", e.getMessage());
            }
        }

        Iterator var23 = flowNode.getOutgoingFlows().iterator();

        while (var23.hasNext()) {
            SequenceFlow sequenceFlow = (SequenceFlow) var23.next();
            highLighted = highLightedFlows.contains(sequenceFlow.getId());
            String defaultFlow = null;
            if (flowNode instanceof Activity) {
                defaultFlow = ((Activity) flowNode).getDefaultFlow();
            } else if (flowNode instanceof Gateway) {
                defaultFlow = ((Gateway) flowNode).getDefaultFlow();
            }

            boolean isDefault = false;
            if (defaultFlow != null && defaultFlow.equalsIgnoreCase(sequenceFlow.getId())) {
                isDefault = true;
            }

            boolean drawConditionalIndicator = sequenceFlow.getConditionExpression() != null && !(flowNode instanceof Gateway);
            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            FlowElement sourceElement = bpmnModel.getFlowElement(sourceRef);
            FlowElement targetElement = bpmnModel.getFlowElement(targetRef);
            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
            if (graphicInfoList != null && graphicInfoList.size() > 0) {
                graphicInfoList = connectionPerfectionizer(processDiagramCanvas, bpmnModel, sourceElement, targetElement, graphicInfoList);
                int[] xPoints = new int[graphicInfoList.size()];
                int[] yPoints = new int[graphicInfoList.size()];

                for (int i = 1; i < graphicInfoList.size(); ++i) {
                    GraphicInfo graphicInfo = (GraphicInfo) graphicInfoList.get(i);
                    GraphicInfo previousGraphicInfo = (GraphicInfo) graphicInfoList.get(i - 1);
                    if (i == 1) {
                        xPoints[0] = (int) previousGraphicInfo.getX();
                        yPoints[0] = (int) previousGraphicInfo.getY();
                    }

                    xPoints[i] = (int) graphicInfo.getX();
                    yPoints[i] = (int) graphicInfo.getY();
                }

                processDiagramCanvas.drawSequenceflow(xPoints, yPoints, drawConditionalIndicator, isDefault, highLighted);

                GraphicInfo lineCenter = getLineCenter(graphicInfoList);
                if (StringUtils.isNotBlank(sequenceFlow.getName())) {
                    int nameLength = sequenceFlow.getName().length();
                    if (sequenceFlow.getName().length() > 7) {
                        nameLength = 7;
                    }
                    lineCenter.setX(lineCenter.getX() - (nameLength * 7));
                    lineCenter.setY(lineCenter.getY() + 2);
                }
                processDiagramCanvas.drawLabel(sequenceFlow.getName(), lineCenter, false);
            }
        }
    }

    private static void drawHighLight(ActivitiProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo, FlowNode flowNode) {
        processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight(), flowNode);
    }

    protected interface ActivityDrawInstruction {

        void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                  BpmnModel bpmnModel,
                  FlowNode flowNode);
    }

    protected interface ArtifactDrawInstruction {

        void draw(ActivitiProcessDiagramCanvas processDiagramCanvas,
                  BpmnModel bpmnModel,
                  Artifact artifact);
    }
}
