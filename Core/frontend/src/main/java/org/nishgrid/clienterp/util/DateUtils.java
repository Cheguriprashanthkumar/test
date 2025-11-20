package org.nishgrid.clienterp.util;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import java.time.LocalDate;

/**
 * Utility class for DatePicker restrictions and customizations.
 */
public final class DateUtils {

    // Private constructor to prevent instantiation
    private DateUtils() {
    }

    /**
     * Restricts the given DatePicker so that future dates cannot be selected.
     * All future dates will be disabled and styled in light grey.
     *
     * @param datePicker the DatePicker to apply restriction on
     */
    public static void restrictFutureDates(DatePicker datePicker) {
        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && item.isAfter(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #999999;");
                        }
                    }
                };
            }
        });
    }
}
