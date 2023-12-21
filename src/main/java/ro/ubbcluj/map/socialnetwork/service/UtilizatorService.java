package ro.ubbcluj.map.socialnetwork.service;

import ro.ubbcluj.map.socialnetwork.domain.Utilizator;
import ro.ubbcluj.map.socialnetwork.domain.validators.ValidationException;
import ro.ubbcluj.map.socialnetwork.observer.Observable;
import ro.ubbcluj.map.socialnetwork.observer.Observer;
import ro.ubbcluj.map.socialnetwork.repository.PagingRepository.UserDBPagingRepository;
import ro.ubbcluj.map.socialnetwork.repository.Repository;
import ro.ubbcluj.map.socialnetwork.repository.UserDBRepository;
import ro.ubbcluj.map.socialnetwork.repository.paging.Page;
import ro.ubbcluj.map.socialnetwork.repository.paging.Pageable;
import ro.ubbcluj.map.socialnetwork.repository.paging.PageableImplementation;

import java.sql.SQLException;
import java.util.*;

public class UtilizatorService implements Observable {

    UserDBPagingRepository repo;
    UserDBRepository repoSimplu;
    List<Observer> observers = new ArrayList<>();

    public UtilizatorService(UserDBPagingRepository repo, UserDBRepository repoSimplu) {
        this.repoSimplu = repoSimplu;
        this.repo = repo;
    }

    public void adaugaUtilizator(Utilizator utilizator) throws SQLException {
        repo.findAll().forEach(it -> {
            if (Objects.equals(it.getUsername(), utilizator.getUsername()))
                throw new ValidationException("Exista un utilizator cu acest username");

        });

        List<Utilizator> all = (List<Utilizator>) repoSimplu.findAll();
        long id = all.isEmpty() ? 1 : all.get(0).getId() + 1;

        utilizator.setId(id);
        repo.save(utilizator);
        notifyAllObservers();

    }

    public void stergeUtilizator(String username) throws SQLException {
        Long id = searchID(username);
        if(id == null){
            throw new ValidationException("Nu exista utilizatorul");
        }
        repo.delete(id);
        notifyAllObservers();
    }

    private Long searchID(String username) throws SQLException {
        for (Utilizator it : repo.findAll()) {
            if (Objects.equals(it.getUsername(), username)) {
                return it.getId();
            }
        }

        return null;
    }

    public void updateUtilizator(String username, String firstName, String lastName) throws SQLException {
        Utilizator user = findUser(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        repo.update(user);
        notifyAllObservers();
    }

    public Utilizator findUser(String username) throws SQLException {
        for (Utilizator it : repo.findAll()) {
            if (Objects.equals(it.getUsername(), username)) {
                return it;
            }
        }
        return null;
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyAllObservers() throws SQLException {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public boolean verifyLogin(String username, String password) throws SQLException {
        Long idUser = searchID(username);
        Optional<Utilizator> user = repo.findOne(idUser);
        if (user.isPresent()) {
            return Objects.equals(user.get().getPassword(), password);
        } else {
            throw new ValidationException("Utilizatorul nu exista");
        }
    }


    //PAGINARE

    private int page;
    private int size;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Set<Utilizator> getAll() {
        Pageable pageable = new PageableImplementation(this.page, this.size);
        Page<Utilizator> utilizatorPage = repo.findAll(pageable);
        return (Set<Utilizator>) utilizatorPage.getContent();
    }


}
