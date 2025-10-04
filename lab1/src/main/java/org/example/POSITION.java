package org.example;

public enum POSITION {
    PREZES("Prezes",25000,5),
    WICEPREZES("Wiceprezes",18000,4),
    MANAGER("Manager",12000,3),
    PROGRAMISTA("Programista",8000,2),
    STAZYSTA("Stażysta",3000,1);
    private final String name;
    private final double baseSalary;
    private final int hierarchyLevel;

    POSITION(String name, double baseSalary, int hierarchyLevel){
        this.name = name;
        this.baseSalary = baseSalary;
        this.hierarchyLevel = hierarchyLevel;
    }

    public String getName() {
        return name;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }
}
