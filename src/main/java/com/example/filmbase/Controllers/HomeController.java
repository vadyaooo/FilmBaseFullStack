package com.example.filmbase.Controllers;

import com.example.filmbase.Entitys.Cinema;
import com.example.filmbase.Entitys.CinemaUser;
import com.example.filmbase.Entitys.FriendRelationship;
import com.example.filmbase.Entitys.User;
import com.example.filmbase.Repositories.CinameRepositorie;
import com.example.filmbase.Repositories.CinemaUserRepositorie;
import com.example.filmbase.Repositories.FriendRelationshipRepositorie;
import com.example.filmbase.Repositories.UserRepositorie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin
public class HomeController {
    @Autowired
    private UserRepositorie userRepositorie;

    @Autowired
    private CinameRepositorie cinameRepositorie;

    @Autowired
    private CinemaUserRepositorie cinemaUserRepositorie;

    @Autowired
    private FriendRelationshipRepositorie friendRelationshipRepositorie;

    private User processUser;

    @PostMapping("/add")//добавление нового пользователя в базу данных
    public String adder(@RequestBody User user){
        if(userRepositorie.findByLogin(user.getLogin()) != null)
            return "ERRORLOGIN";
        if(userRepositorie.findByEmail(user.getEmail()) != null)
            return "ERROREMAIL";
        user.setShowforaddfr(false);
        user.setShowotherfilms(false);
        userRepositorie.save(user);
        return "GOOD";
    }

    @PostMapping("/autorization")//авторизация пользователя
    public String auth(@RequestBody User user){
        if(userRepositorie.findByLogin(user.getLogin()).getLogin().equals(user.getLogin()) &&
                userRepositorie.findByLogin(user.getLogin()).getPassword().equals(user.getPassword())) {
            processUser = userRepositorie.findByLogin(user.getLogin());
            return "AUTH";
        }
        else
            return "NOAUTH";
    }

    @GetMapping("/allmyfriend")//передать клиенту всех друзей текущего авторизованного пользователя
    public List<FriendRelationship> allmyfriend(){
        return friendRelationshipRepositorie.findFriendRelationshipsByUsersone_Id(processUser.getId());
    }

    @PostMapping("/allmyfriendsort")//сортировка друзей текущего авторизованного пользователя по логину
    public List<FriendRelationship> allmyfriendsort(@RequestBody User user){
        return friendRelationshipRepositorie.findFriendRelationshipsByUserstwo_LoginContainingAndUsersone_Id(user.getLogin(), processUser.getId());
    }

    @GetMapping("/allusersforfriend")//передать клиенту всех пользователей, у которых разрешено добавление в друзья другими пользователями
    public List<User> allusersforfriend(){
        List<User> userList = userRepositorie.findByShowforaddfr(true);
        return userList;
    }

    @PostMapping("/allusersforfriendsort")//сортировка пользователей для добавление в друзья
    public List<User> allusersforfriendsort(@RequestBody User user){
        List<User> userList = userRepositorie.findByLoginContainingAndShowforaddfr(user.getLogin(), true);
        return userList;
    }



    @PostMapping("/addmyfriend")//добавление выбранного пользователя в друзья к текущему авторизованному пользователю, производиться проверка
    //если пользователь уже есть в друзьях у авторизованного, то ошибка
    public String addmyfriend(@RequestBody User user){
        if(user.getId() == processUser.getId())
            return "ERROR";
        if (friendRelationshipRepositorie.findFriendRelationshipsByUsersone_IdAndUserstwo_Id(processUser.getId(), user.getId()) != null)
            return "ERROR1";
        else {
            FriendRelationship friendRelationship = new FriendRelationship();
            friendRelationship.setUsersone(processUser);
            friendRelationship.setUserstwo(userRepositorie.findById(user.getId()));
            friendRelationshipRepositorie.save(friendRelationship);
            return "GOOD";
        }
    }


    @PostMapping("/deleteonmyfriend")//удаление пользователя из друзей текущего авторизованного пользователя
    public String deleteonmyfriend(@RequestBody FriendRelationship friendRelationship){
        if (friendRelationshipRepositorie.findFriendRelationshipsByUsersone_IdAndUserstwo_Id(processUser.getId(), friendRelationship.getUserstwo().getId()) != null) {
            FriendRelationship friendRelationship1 = friendRelationshipRepositorie.findFriendRelationshipsByUsersone_IdAndUserstwo_Id(processUser.getId(), friendRelationship.getUserstwo().getId());
            friendRelationshipRepositorie.delete(friendRelationship1);
            return "GOOD";
        }
        else {
            return "ERROR";
        }
    }

    @GetMapping("/deauth")//в отсутствие использования spring security вручную производим логаут, в данном случае очищаем экземпляр класса User
    public String deauth(){
        processUser = null;
        return "DEAUTH";
    }



    @PostMapping("/savechangemyaccount")//сохранение настроек приватности текущего авторизованного пользователя
    public String savechangemyaccount(@RequestBody User user){
        processUser.setShowotherfilms(user.isShowotherfilms());
        processUser.setShowforaddfr(user.isShowforaddfr());
        userRepositorie.save(processUser);
        return "1. Настройки успешно изменены";
    }

    @PostMapping("/savemynewpass")//сохранение нового праоля, вместе с проверкой на валидность нового и старого
    public String savemynewpass(@RequestBody User user){
        if(!user.getLogin().equals(processUser.getPassword()))
            return "2. Старый пароль введен неверно";
        else{
            if(!user.getPassword().equals(user.getEmail()))
                return "3. Новый пароль не совпадает";
            else {
                processUser.setPassword(user.getPassword());
                userRepositorie.save(processUser);
                return "1. Пароль успешно изменен";
            }
        }
    }

    @GetMapping("/getprocessuser2")//проверка на авторизацию пользователя в системе
    public String rs2(){
        if(processUser == null)
            return "NULL";
        if(processUser != null && processUser.getLogin().equals("admin"))
            return "ADMIN";
        else
            return "USER";

    }

    @GetMapping("/getAccount")//получение данных аккаунта
    public User getAccount(){
        User user1 = userRepositorie.findById(processUser.getId());
        user1.setPassword(" ");
        return user1;
    }

    @GetMapping("/allfilms")//передать на сторону клиента все фильмы, которые есть в базе
    public List<Cinema> allfilms(){
        return cinameRepositorie.findAll();
    }

    @PostMapping("/allfilmssort")//отсортировать фильмы либо по жанру, либо по названию, либо по режисеру, причем
    //используем для этого один переданный на сервер параметр, отправленных в поле названия
    public List<Cinema> allfilmssort(@RequestBody Cinema cinema){
        List<Cinema> cinema1 = cinameRepositorie.findByHeadnameContainingOrZhanrContainingOrDirectorContaining(cinema.getHeadname(), cinema.getHeadname(), cinema.getHeadname());
        return cinema1;
    }

    @GetMapping("/allmyfilms")//все фильмы текущего авторизованного пользователя
    public List<CinemaUser> allmyfilms(){
        List<CinemaUser> fer = cinemaUserRepositorie.findAllByUsers(processUser);
        return fer;
    }


    @PostMapping("/showusercollection")//отобразить коллекцию выбранного из друзей пользователя
    public List<CinemaUser> showusercollection(@RequestBody User user){
        User user1 = userRepositorie.findById(user.getId());
        List<CinemaUser> cinemaUser = cinemaUserRepositorie.findAllByUsers(user1);
        return cinemaUser;
    }

    @PostMapping("/showusercollectionsort")//отсортировать фильмы из колекции пользователя-друга текущего авторизованного
    public List<CinemaUser> showusercollectionsort(@RequestBody Cinema cinema){
        User user1 = userRepositorie.findById(cinema.getId());
        List<CinemaUser> cinemaUser = cinemaUserRepositorie.findCinemaUsersByCinemas_HeadnameContainingAndUsers(cinema.getHeadname(), user1);
        return cinemaUser;
    }

    @PostMapping("/allmyfilmssort")//сортировка фильмов коллекци текущего авторизованного пользователя
    public List<CinemaUser> allmyfilmssort(@RequestBody Cinema cinema){
        return cinemaUserRepositorie.findCinemaUsersByCinemas_HeadnameContainingAndUsers(cinema.getHeadname(), processUser);
    }

    @PostMapping("/addmyfilm")//добавление фильма в коллекцию текущего авторизованного пользователя
    public String addmyfilm(@RequestBody Cinema cinema){
        if(cinemaUserRepositorie.findByCinemasAndUsers(cinema, processUser) != null) {
            return "ERROR";
        }
        else {
            CinemaUser cinemaUser = new CinemaUser();
            cinemaUser.setCinemas(cinema);
            cinemaUser.setUsers(processUser);
            cinemaUser.setStatuscinema("Без статуса просмотра");
            cinemaUser.setRatinguser(0);
            cinemaUserRepositorie.save(cinemaUser);
            return "GOOD";
        }
    }

    @PostMapping("/addnewfilm")//добавление нового фильма в базу
    public String addnewfilm(@RequestBody Cinema cinema){
        if(cinameRepositorie.findByHeadnameAndYear(cinema.getHeadname(), cinema.getYear()).size() != 0) {
            return "Этот фильм уже есть в базе";
        }
        else {
            cinameRepositorie.save(cinema);
            return "1. Фильм успешно добавлен в каталог";
        }
    }


    @PostMapping("/editfilm")//редактирование выбранного фильма, данной функией обладает только администратор
    public String editfilm(@RequestBody Cinema cinema){
        Cinema cinema1 = cinameRepositorie.findById(cinema.getId());
        cinema1.setHeadname(cinema.getHeadname());
        cinema1.setAboutis(cinema.getAboutis());
        cinema1.setDirector(cinema.getDirector());
        cinema1.setUrlimage(cinema.getUrlimage());
        cinema1.setYear(cinema.getYear());
        cinema1.setZhanr(cinema.getZhanr());
        cinameRepositorie.save(cinema1);
        return "1. Изменения успешно сохранены";
    }

    @PostMapping("/deleteonemyfilm")//удаление фильма из коллекции фильмов текущего авторизованного пользователя
    public String deleteonemyfilm(@RequestBody Cinema cinema){
        if(cinemaUserRepositorie.findByCinemasAndUsers(cinema, processUser) != null) {
            CinemaUser cinemaUser = cinemaUserRepositorie.findByCinemasAndUsers(cinema, processUser);
            cinemaUserRepositorie.delete(cinemaUser);
            return "1. Фильм успешно удален из вашего каталога";
        }
        else {
            return "Произошла ошибка, повторите попытку позже";
        }
    }


    @PostMapping("/savechangemyfilm")//сохранение оценки к фильму от пользователя, у которого этот фильм находиться в коллекции
    public String savechangemyfilm(@RequestBody CinemaUser cinemaUser){
        CinemaUser cinemaUser1 = cinemaUserRepositorie.findById(cinemaUser.getId());
        if(cinemaUser1 != null) {//проверка есть ли этот фильм в базе
            if(cinemaUser.getRatinguser() != 0){//проверка, оценивал ли этот фильм пользователь ранее
                Cinema cinema = cinemaUser1.getCinemas();
                int kolvoocenok = cinema.getMarks();
                float ocenka = cinema.getRating(), promocenka = cinemaUser1.getRatinguser();
                if (kolvoocenok == 0) {//если количество оценок равно нулю, то есть фильм не имеет оценок на портале
                    cinema.setRating(cinemaUser.getRatinguser());
                    cinema.setMarks(1);
                }
                if(kolvoocenok > 0 && promocenka != 0){//если оценки к фильму уже есть и есть оценка от пользователя
                    cinema.setRating((kolvoocenok * ocenka + cinemaUser.getRatinguser() - promocenka)/ (kolvoocenok));
                    cinema.setMarks(cinema.getMarks());
                }
                if(kolvoocenok > 0 && promocenka == 0){
                    cinema.setRating((kolvoocenok * ocenka + cinemaUser.getRatinguser())/ (kolvoocenok + 1));
                    cinema.setMarks(cinema.getMarks() + 1);
                }
                cinemaUser1.setCinemas(cinema);
                cinemaUser1.setRatinguser(cinemaUser.getRatinguser());
            }

            cinemaUser1.setStatuscinema(cinemaUser.getStatuscinema());
            cinemaUserRepositorie.save(cinemaUser1);
            return "1. Изменения успешно сохранены";
        }
        else {
            return "Произошла ошибка, повторите попытку позже";
        }
    }
}