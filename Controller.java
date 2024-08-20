import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.input.MouseEvent;



import java.util.Stack;

public class Controller {

    @FXML
    private Pane shapePane;
    @FXML
    private ComboBox<String> shapeSelector;
    @FXML
    private ComboBox<String> fillSelector;
    @FXML
    private ColorPicker colorSelector;


    private String currentShape = null;
    private String fillType = null;
    private Color currentColor = null;
    private final Stack<Node> shapes = new Stack<>();
    private double startX, startY; // Starting points for the shape
    private Shape currentShapeNode;

    @FXML
    public void initialize() {
        // Initialize ComboBoxes with values for shapes and fill types
        shapeSelector.setItems(FXCollections.observableArrayList("Rectangle", "Circle", "Line", "Ellipse"));
        fillSelector.setItems(FXCollections.observableArrayList("Fill", "Transparent"));
        shapePane.setCursor(Cursor.CROSSHAIR);

        // Setup listeners for UI controls to update current settings
        colorSelector.valueProperty().addListener((obs, oldVal, newVal) -> currentColor = newVal);
        shapeSelector.valueProperty().addListener((obs, oldVal, newVal) -> currentShape = newVal);
        fillSelector.valueProperty().addListener((obs, oldVal, newVal) -> fillType = newVal);
    }

    // Method to clear all shapes from the drawing pane and the stack
    public void clearShapes() {
        shapePane.getChildren().clear();
        currentShape = null;
    }

    // Method to remove the last shape drawn (undo)
    public void undoLastShape() {
        if (!shapes.isEmpty()) {
            Node lastShape = shapes.pop();
            shapePane.getChildren().remove(lastShape);
        }
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        // Record the initial mouse position when the user begins to draw
        startX = event.getX();
        startY = event.getY();
        // Initialize a placeholder shape based on the selected type, positioned at start coordinates
        if (currentShape != null) {
            switch (currentShape) {
                case "Rectangle":
                    currentShapeNode = new Rectangle(startX, startY, 0, 0);
                    break;
                case "Circle":
                    currentShapeNode = new Circle(startX, startY, 0);
                    break;
                case "Line":
                    currentShapeNode = new Line(startX, startY, startX, startY);
                    break;
                case "Ellipse":
                    currentShapeNode = new Ellipse(startX, startY, 0, 0);

                    break;
            }
            // Add the new shape to the pane for drawing
            shapePane.getChildren().add(currentShapeNode);
            // Configure the shape's color and fill properties
            configureShape(currentShapeNode);
            // Push the shape onto a stack to allow for undo
            shapes.push(currentShapeNode);
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        // Update the shape's dimensions as the user drags the mouse
        if (currentShapeNode != null) {
            double endX = event.getX();
            double endY = event.getY();

            switch (currentShape) {
                case "Rectangle":
                    // Adjust the rectangle's size and position
                    adjustRectangle((Rectangle) currentShapeNode, startX, startY, endX, endY);
                    break;
                case "Circle":
                    // Adjust the circle's size and position
                    adjustCircle((Circle) currentShapeNode, startX, startY, endX, endY);
                    break;
                case "Line":
                    // Update the line's endpoint
                    Line line = (Line) currentShapeNode;
                    line.setEndX(endX);
                    line.setEndY(endY);
                    break;
                case "Ellipse":
                    // Adjust the ellipse's size and position
                    adjustEllipse((Ellipse) currentShapeNode, startX, startY, endX, endY);
                    break;
            }
        }
    }

    @FXML
    private void handleMouseReleased() {
        // Marking the end of drawing interaction
        currentShapeNode = null;
    }


    private void adjustRectangle(Rectangle rectangle, double startX, double startY, double endX, double endY) {
        // Calculate the top-left corner of the rectangle
        double minX = Math.min(startX, endX);
        double minY = Math.min(startY, endY);

        // Calculate the width and height ensuring they are positive
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        // Set the rectangle's properties
        rectangle.setX(minX);
        rectangle.setY(minY);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
    }


    private void adjustCircle(Circle circle, double startX, double startY, double endX, double endY) {
        // Calculate the radius using Euclidean formula
        double radius = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));

        // Set the properties of the circle
        circle.setCenterX(startX + (endX - startX) / 2);
        circle.setCenterY(startY + (endY - startY) / 2);
        circle.setRadius(radius / 2);
    }


    private void adjustEllipse(Ellipse ellipse, double startX, double startY, double endX, double endY) {
        // Calculate the width and height based on cursor position relative to the start point
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        // Determine the center of the ellipse
        double centerX = startX + (endX - startX) / 2;
        double centerY = startY + (endY - startY) / 2;

        // Set the properties of the ellipse
        ellipse.setCenterX(centerX);
        ellipse.setCenterY(centerY);
        ellipse.setRadiusX(width / 2);
        ellipse.setRadiusY(height / 2);
    }

    // Configure the visual properties of a shape based on user selections
    private void configureShape(Shape shape) {
        if (shape == null) {
            return;  // Avoid further processing if shape is null
        }
        if (currentColor == null) {
            currentColor = Color.WHITE;  // Default to white if no color selected
        }
        if (fillType == null) {
            fillType = "Fill";  // Default fill type
        }
        shape.setStroke(currentColor); // Set the stroke color based on selection
        if (fillType.equals("Transparent")) {
            shape.setFill(null);
        } else {
            shape.setFill(currentColor); // Set the fill color based on selection
        }
    }

}
