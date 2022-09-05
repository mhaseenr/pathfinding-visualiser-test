package com.jpro.pathfinding;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseHandler implements EventHandler<MouseEvent>{

    private final EventHandler<MouseEvent> onDraggedEventHandler;
    private final EventHandler<MouseEvent> onClickedEventHandler;
    private EventHandler<MouseEvent> onPressedEventHandler;
    private EventHandler<MouseEvent> onReleasedEventHandler;

    public static boolean dragging = false;

    public MouseHandler(EventHandler<MouseEvent> onDraggedEventHandler,
                        EventHandler<MouseEvent> onClickedEventHandler,
                        EventHandler<MouseEvent> onPressedEventHandler,
                        EventHandler<MouseEvent> onReleasedEventHandler) {
        this.onDraggedEventHandler = onDraggedEventHandler;
        this.onClickedEventHandler = onClickedEventHandler;
        this.onPressedEventHandler = onPressedEventHandler;
        this.onReleasedEventHandler = onReleasedEventHandler;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            dragging = false;
            onPressedEventHandler.handle(event);
        }
        else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (dragging) {
                dragging = false;
                onReleasedEventHandler.handle(event);
            }

        }
        else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            dragging = true;
            onDraggedEventHandler.handle(event);
        }
        else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (!dragging) {
                onClickedEventHandler.handle(event);
            }
        }
    }
}
