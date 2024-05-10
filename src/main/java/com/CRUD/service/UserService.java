package com.CRUD.service;


import com.CRUD.entity.User;
import com.CRUD.errorService.UserNotFoundException;
import com.CRUD.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void saveUser(User userEntity){
        userRepository.save(userEntity);
    }

    public void deleteUserById(long id){
        userRepository.deleteById(id);
    }

    public User getUserById(long id){
        Optional<User> optionalUser= userRepository.findById(id);
        if(optionalUser.isPresent()){
            return optionalUser.get();

        }

        throw new UserNotFoundException("User not found for id: " +  id);

    }

    public List<User> findUsersByBirthdayBetween(Date fromDate, Date toDate) {
        return userRepository.findByBirthdayBetween(fromDate, toDate);
    }

    public Page<User> findPaginated(int pageNumber, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        if (sortField.equals("numericField")) {
            sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                    Sort.by(sortField).ascending().and(Sort.by("id").ascending()) :
                    Sort.by(sortField).descending().and(Sort.by("id").descending());
        }

        Pageable pageable = PageRequest.of(pageNumber-1,pageSize,sort);
        return userRepository.findAll(pageable);
    }


}
