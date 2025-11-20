package org.nishgrid.clienterp.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceRecordFx {
    private final ObjectProperty<EmployeeFx> employee = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> checkIn = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> checkOut = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty workingHours = new SimpleStringProperty();

    public AttendanceRecordFx(EmployeeFx employee, LocalDate date, LocalTime checkIn, LocalTime checkOut, String status, String workingHours) {
        this.employee.set(employee);
        this.date.set(date);
        this.checkIn.set(checkIn);
        this.checkOut.set(checkOut);
        this.status.set(status);
        this.workingHours.set(workingHours);
    }

    // Getters
    public EmployeeFx getEmployee() { return employee.get(); }
    public LocalDate getDate() { return date.get(); }
    public LocalTime getCheckIn() { return checkIn.get(); }
    public LocalTime getCheckOut() { return checkOut.get(); }
    public String getStatus() { return status.get(); }
    public String getWorkingHours() { return workingHours.get(); }


    // Property Getters for JavaFX
    public ObjectProperty<EmployeeFx> employeeProperty() { return employee; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public ObjectProperty<LocalTime> checkInProperty() { return checkIn; }
    public ObjectProperty<LocalTime> checkOutProperty() { return checkOut; }
    public StringProperty statusProperty() { return status; }
    public StringProperty workingHoursProperty() { return workingHours; }
}