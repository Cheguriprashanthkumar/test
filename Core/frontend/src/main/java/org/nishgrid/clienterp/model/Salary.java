package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class Salary{

    private LongProperty id = new SimpleLongProperty();
    private ObjectProperty<EmployeeFx> employee = new SimpleObjectProperty<>();

    private DoubleProperty basicSalary = new SimpleDoubleProperty();
    private DoubleProperty hra = new SimpleDoubleProperty();
    private DoubleProperty otherAllowances = new SimpleDoubleProperty();
    private DoubleProperty pfDeduction = new SimpleDoubleProperty();
    private DoubleProperty esiDeduction = new SimpleDoubleProperty();
    private DoubleProperty tdsDeduction = new SimpleDoubleProperty();
    private DoubleProperty totalSalary = new SimpleDoubleProperty();
    private DoubleProperty yearSalary = new SimpleDoubleProperty();

    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public EmployeeFx getEmployee() { return employee.get(); }
    public void setEmployee(EmployeeFx emp) { this.employee.set(emp); }
    public ObjectProperty<EmployeeFx> employeeProperty() { return employee; }

    public double getBasicSalary() { return basicSalary.get(); }
    public void setBasicSalary(double val) { this.basicSalary.set(val); }
    public DoubleProperty basicSalaryProperty() { return basicSalary; }

    public double getHra() { return hra.get(); }
    public void setHra(double val) { this.hra.set(val); }
    public DoubleProperty hraProperty() { return hra; }

    public double getOtherAllowances() { return otherAllowances.get(); }
    public void setOtherAllowances(double val) { this.otherAllowances.set(val); }
    public DoubleProperty otherAllowancesProperty() { return otherAllowances; }

    public double getPfDeduction() { return pfDeduction.get(); }
    public void setPfDeduction(double val) { this.pfDeduction.set(val); }
    public DoubleProperty pfDeductionProperty() { return pfDeduction; }

    public double getEsiDeduction() { return esiDeduction.get(); }
    public void setEsiDeduction(double val) { this.esiDeduction.set(val); }
    public DoubleProperty esiDeductionProperty() { return esiDeduction; }

    public double getTdsDeduction() { return tdsDeduction.get(); }
    public void setTdsDeduction(double val) { this.tdsDeduction.set(val); }
    public DoubleProperty tdsDeductionProperty() { return tdsDeduction; }

    public double getTotalSalary() { return totalSalary.get(); }
    public void setTotalSalary(double val) { this.totalSalary.set(val); }
    public DoubleProperty totalSalaryProperty() { return totalSalary; }

    public double getYearSalary() { return yearSalary.get(); }
    public void setYearSalary(double val) { this.yearSalary.set(val); }
    public DoubleProperty yearSalaryProperty() { return yearSalary; }
}
