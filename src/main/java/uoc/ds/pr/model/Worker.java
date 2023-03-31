package uoc.ds.pr.model;

import java.time.LocalDate;

public class Worker {

    private String dni;
    private String name;
    private String surname;
    private LocalDate birthday;
    private Role role;

    public Worker(String dni, String name, String surname, LocalDate birthday, Role role) {
        this.dni = dni;
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.role = role;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Role getRole() {
        return role;
    }

    public String getRoleId() {
        return role.getRoleId();
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Worker w)) {
            return false;
        }
        return w.getDni().equals(dni);
    }
}
