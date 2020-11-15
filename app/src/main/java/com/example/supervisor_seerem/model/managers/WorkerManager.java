package com.example.supervisor_seerem.model.managers;

import androidx.annotation.NonNull;

import com.example.supervisor_seerem.model.Worker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Singleton class
 * Holds all the Worker objects
 * This can possibly have an SQL database
 */
public class WorkerManager implements Iterable<Worker> {
    List<Worker> workers = new ArrayList<>();

    public List<Worker> getWorkers() {
        return workers;
    }

    private WorkerManager() {
        // do nothing (to prevent object instantiation)
    }

    /*
     * Singleton Support
     */
    private static WorkerManager instance;
    public static WorkerManager getInstance() {
        if(instance == null) {
            instance = new WorkerManager();
        }
        return instance;
    }

    public void add(Worker worker) {
        workers.add(worker);
    }

    public int getManagerSize() {
        return workers.size();
    }

    public Worker findWorkerById(String id) {
        for(Worker w: workers) {
            if(w.getEmployeeID().equals(id)) {
                return w;
            }
        }

        return null;
    }

    public Worker findWorkerByFirstName(String first_name) {
        for(Worker w: workers) {
            if(w.getFirstName().equals(first_name)) {
                return w;
            }
        }

        return null;
    }

    public Worker findWorkerBySiteId(String id) {
        for(Worker w: workers) {
            if(w.getWorksiteID().equals(id)) {
                return w;
            }
        }

        return null;
    }

    // TODO: Change the function to match the highest number of matches in skills
    public List<Worker> findWorkerBySkills(List<String> skills) {
        List<Worker> filtered = new ArrayList<>();

        if(skills.isEmpty()) {
            return getWorkers();
        }

        for (Worker w: workers) {
            List<String> skillSet= w.getSkills();
            skillSet.retainAll(skills);
            if(skillSet.size() > 0) {
                filtered.add(w);
            }
        }

        return filtered;
    }

//    private class DataClass {
//        int size;
//        Worker worker;
//
//        public DataClass(Worker worker, int size) {
//            this.size = size;
//            this.worker = worker;
//        }
//    }

    @NonNull
    @Override
    public Iterator<Worker> iterator() {
        return getWorkers().iterator();
    }
}