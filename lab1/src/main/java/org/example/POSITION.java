package org.example;

public enum POSITION {
    PREZES(25000,5),
    WICEPREZES(18000,4),
    MANAGER(12000,3),
    PROGRAMISTA(8000,2),
    STAZYSTA(3000,1);
    private final double baseSalary;
    private final int hierarchyLevel;

    POSITION(double baseSalary, int hierarchyLevel){
        this.baseSalary = baseSalary;
        this.hierarchyLevel = hierarchyLevel;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }
}
