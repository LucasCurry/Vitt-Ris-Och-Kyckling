package com.example;

import com.example.repos.AccountRepo;
import com.example.repos.AccountService;
import com.example.repos.TaskRepo;
import com.example.repos.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
public class WebController {

    @Autowired
    AccountService accService;
    @Autowired
    AccountRepo accountRepo;
    @Autowired
    TaskRepo taskRepo;

    @Autowired
    PasswordEncoder passEnco;

    @Autowired
    TaskService taskService;


    //Frontpage Controller
    @GetMapping("/")
    String home(Model model) {
        Long id = accService.getAccountId();
        model.addAttribute("task", taskRepo.findAll());
        return "home";
    }
    @PostMapping("/")
    String postHome(Model model, @RequestParam(required = false, defaultValue = "") String cities, @RequestParam(required = false, defaultValue = "") String sorting) {
        System.out.println(taskRepo.findAllByCity(cities));
        if(cities.equals("") && sorting.equals("")) {
            return "redirect:/";
        }
        else if(sorting.equals("")) {
            model.addAttribute("task",taskRepo.findAllByCity(cities));
            return "home";
        }
        return "home";
    }

/*    @PostMapping("/")
    String sortCity(Model model, @RequestParam String cities) {

        return "redirect:/{city}";
    }

    @GetMapping("/{city}")
    String sortedByCity (Model model, String city) {
        model.addAttribute("task", taskRepo.findAllByCity(city));
        return "home";
    }*/

    //TaskPage Controller
    @GetMapping("/task/{id}")
    String task(Model model, @PathVariable Long id) {
        model.addAttribute("task",taskRepo.findById(id).get());
        return "task";
    }

    //Login Controllers
    @GetMapping("/login")
    String login() {
        return "login";
    }

    /*@PostMapping("/login")
    String loggedIn(Model model, @RequestParam String username, @RequestParam String password){
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        model.addAttribute("accountId", accountRepo.findByUsername(username).getId());
        System.out.println(username);
        System.out.println(password);
        Account account = accountRepo.findByUsernameAndPassword(username, password);
        if (account != null){
            return "redirect:/account/" + account.id;
        }
        return "login";
    }*/

    @GetMapping("/account")
    String accountpage(Model model) {
        Long id = accService.getAccountId();
        model.addAttribute("accountId", id);
        model.addAttribute("account", accountRepo.findById(id).get().username);
        model.addAttribute("task", taskRepo.findAllByAccountId(id));
        return "accountpage";
    }

    @GetMapping("/account/create")
    String createTask(Model model) {
        Long id = accService.getAccountId();
        model.addAttribute("accountId", id);
        return "createTask";
    }

    @PostMapping ("/account/create")
    String postCreateTask(Model model, @RequestParam String title, @RequestParam String description, @RequestParam String cities, @RequestParam int price, @RequestParam String image) {
        Long id = accService.getAccountId();
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("price", price);
        model.addAttribute("image", image);
        model.addAttribute("accountId", id);
        Task task = new Task(title, accountRepo.findById(id).get().address, cities, image, price, description, id);
        taskService.addTask(task);
        return "redirect:/";
    }


    //Access testing
    @GetMapping("/secret")
    String secret() {
        return "secret";
    }



    //Registration Controllers
    @GetMapping("/register")
    String register(){
        return "register";
    }

    @PostMapping("/register")
    String registerUser(@RequestParam String firstname, @RequestParam String lastname,@RequestParam String username, @RequestParam String password, @RequestParam String passwordControll, @RequestParam String email, @RequestParam String phonenumber, @RequestParam String address, @RequestParam String cardnumber){
        if (accountRepo.findByUsername(username) == null){
            if (accountRepo.findByEmail(email) == null){
                if (password.equals(passwordControll)){
                    Account account = new Account(firstname, lastname,username,passEnco.encode(password), phonenumber, email, address, cardnumber);
                    accountRepo.save(account);
                    return "redirect:/login";
                }
            }
        }

        return "register";
    }

    @GetMapping("/chat")
    String chat() {
        return "chat";
    }
}
