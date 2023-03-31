package uoc.ds.pr.model;

import edu.uoc.ds.adt.helpers.Position;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;
import edu.uoc.ds.traversal.Traversal;

public class Role {

    private String roleId;
    private String description;
    private List<Worker> workers;

    public Role(String roleId, String description) {
        this.roleId = roleId;
        this.description = description;
        this.workers = new LinkedList<>();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Iterator<Worker> getWorkers() {
        return workers.values();
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public void addWorker(Worker worker) {
        this.workers.insertEnd(worker);
    }

    public void deleteWorker(Worker worker) {
        Traversal<Worker> positions = workers.positions();
        while (positions.hasNext()) {
            Position<Worker> position = positions.next();
            if (position.getElem().equals(worker)) {
                workers.delete(position);
            }
        }
    }

    public int numWorkers() {
        return workers.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Role r)) {
            return false;
        }
        return r.getRoleId().equals(roleId);
    }
}
