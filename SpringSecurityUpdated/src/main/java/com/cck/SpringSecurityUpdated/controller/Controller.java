package com.cck.SpringSecurityUpdated.controller;

import com.cck.SpringSecurityUpdated.model.OurUser;
import com.cck.SpringSecurityUpdated.model.Product;
import com.cck.SpringSecurityUpdated.repository.OurUserRepo;
import com.cck.SpringSecurityUpdated.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping
public class Controller {

    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired

    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String goHome(){
        return "This is publicly accessible without needing authentication";
    }

    @PostMapping("/user/save")
    public ResponseEntity<Object> saveUser(@RequestBody OurUser ourUser){
        ourUser.setPassword(passwordEncoder.encode(ourUser.getPassword()));
        OurUser result = ourUserRepo.save(ourUser);
        if(result.getId() > 0){
            return ResponseEntity.ok("User save Successfully");
        }
        return ResponseEntity.status(404).body("Error, User not Saved");

    }

    @PostMapping("/product/save")
    public ResponseEntity<Object> saveProduct(@RequestBody Product product){
        Product  result = productRepo.save(product);
        if(result.getId()>0){
            return ResponseEntity.ok("Product added successfully");
        }
        return ResponseEntity.status(404).body("Error, product not found");
    }

    @GetMapping("/product/all")
    public ResponseEntity<Object> getAllProducts(){
        return ResponseEntity.ok(productRepo.findAll());
    }

    @GetMapping("/users/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> getAllUsers(){
        return ResponseEntity.ok(ourUserRepo.findAll());
    }

    @GetMapping("/users/single") //the use that is logged in can get his own details
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Object> getMyDetails(){
        return ResponseEntity.ok(ourUserRepo.findByEmail(getLoggedInUserDetails().getUsername()));
    }

    public UserDetails getLoggedInUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails){
            return (UserDetails) authentication.getPrincipal();
        }

        return null;
    }


}
