package ro.ubbcluj.map.socialnetwork.service;

import ro.ubbcluj.map.socialnetwork.domain.Message;
import ro.ubbcluj.map.socialnetwork.domain.Tuple;
import ro.ubbcluj.map.socialnetwork.domain.validators.ValidationException;
import ro.ubbcluj.map.socialnetwork.observer.Observable;
import ro.ubbcluj.map.socialnetwork.observer.Observer;
import ro.ubbcluj.map.socialnetwork.repository.MessageDBRepository;
import ro.ubbcluj.map.socialnetwork.repository.PrietenieDBRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements Observable {

    private MessageDBRepository repo;
    private PrietenieDBRepository prietenieDBRepository;

    private final List<Observer> observers = new ArrayList<>();

    public MessageService(MessageDBRepository repo, PrietenieDBRepository prietenieDBRepository) {
        this.repo = repo;
        this.prietenieDBRepository = prietenieDBRepository;
    }

    public void addMessage(Message message) throws SQLException {
        if(prietenieDBRepository.findOne(new Tuple<>(message.getIdReceiver(), message.getIdSender())).isEmpty()
        && prietenieDBRepository.findOne((new Tuple<>(message.getIdSender(), message.getIdReceiver()))).isEmpty()) {
            throw new ValidationException("Nu esti prieten cu " + message.getIdReceiver() + " deci nu ii poti trimite mesaje!");
        }

        repo.save(message);
        notifyAllObservers();
    }

//    public void addReply(Long id, Message message)
//    {
//        message.getReply().add(id);
//        repo.update(message);
//    }

    public List<Message> conversation(Long id1, Long id2) {
        List<Message> allMessages = (List<Message>) getAll();
        List<Message> conversationMessages = new ArrayList<>();

        for (Message message : allMessages) {
            if (isPartOfConversation(message, id1, id2)) {
                conversationMessages.add(message);
            }
        }

        return conversationMessages;
    }

    private boolean isPartOfConversation(Message message, Long id1, Long id2) {
        return (message.getIdSender().equals(id1) && message.getIdReceiver().equals(id2)) ||
                (message.getIdSender().equals(id2) && message.getIdReceiver().equals(id1));
    }



    private Iterable<Message> getAll()
    {
        return repo.findAll();
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
}
