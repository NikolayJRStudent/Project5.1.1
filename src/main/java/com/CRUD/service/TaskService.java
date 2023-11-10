package com.CRUD.service;

import com.CRUD.entity.Task;
import com.CRUD.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    public void saveTask(Task taskEntity){
        taskRepository.save(taskEntity);
    }

    public void deleteTaskById(long id){
        taskRepository.deleteById(id);
    }

    public Task getTaskById(long id){
        Optional<Task> optionalTask= taskRepository.findById(id);
        if(optionalTask.isPresent()){
            return optionalTask.get();

        }

        throw new RuntimeException("Task not found for id: "+ id);

    }

    public Page<Task> findPaginated(int pageNumber, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        if (sortField.equals("numericField")) {
            sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                    Sort.by(sortField).ascending().and(Sort.by("id").ascending()) :
                    Sort.by(sortField).descending().and(Sort.by("id").descending());
        }

        Pageable pageable = PageRequest.of(pageNumber-1,pageSize,sort);
        return taskRepository.findAll(pageable);
    }


}
