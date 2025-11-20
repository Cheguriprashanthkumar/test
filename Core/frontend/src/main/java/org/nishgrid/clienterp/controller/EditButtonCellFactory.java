package org.nishgrid.clienterp.controller;



import javafx.scene.control.*;
import javafx.util.Callback;
import org.nishgrid.clienterp.controller.EditExitController;
import org.nishgrid.clienterp.model.EmployeeExit;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditButtonCellFactory implements Callback<TableColumn<EmployeeExit, Void>, TableCell<EmployeeExit, Void>> {

    @Override
    public TableCell<EmployeeExit, Void> call(final TableColumn<EmployeeExit, Void> param) {
        return new TableCell<>() {
            private final Button btn = new Button("Edit");

            {
                btn.setOnAction(e -> {
                    EmployeeExit data = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_exit.fxml"));
                        Parent root = loader.load();
                        EditExitController controller = loader.getController();
                        controller.setEditMode(true, data);

                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(new Scene(root));
                        stage.setTitle("Edit Exit");
                        stage.showAndWait();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
    }
}
