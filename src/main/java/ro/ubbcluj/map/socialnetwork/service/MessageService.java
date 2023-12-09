package ro.ubbcluj.map.socialnetwork.service;

import ro.ubbcluj.map.socialnetwork.domain.Message;
import ro.ubbcluj.map.socialnetwork.domain.Tuple;
import ro.ubbcluj.map.socialnetwork.domain.validators.ValidationException;
import ro.ubbcluj.map.socialnetwork.observer.Observable;
import ro.ubbcluj.map.socialnetwork.observer.Observer;
import ro.ubbcluj.map.socialnetwork.repository.MessageDBRepository;
import ro.ubbcluj.map.socialnetwork.repository.PrietenieDBRepository;
import ro.ubbcluj.map.socialnetwork.repository.UserDBRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements Observable {

    private final MessageDBRepository repo;
    private final PrietenieDBRepository prietenieDBRepository;
    private final UserDBRepository userDBRepository;

    private final List<Observer> observers = new ArrayList<>();

    public MessageService(MessageDBRepository repo, PrietenieDBRepository prietenieDBRepository, UserDBRepository userDBRepository) {
        this.repo = repo;
        this.prietenieDBRepository = prietenieDBRepository;
        this.userDBRepository = userDBRepository;
    }

    public void addMessage(Message message) throws SQLException {
        if(prietenieDBRepository.findOne(new Tuple<>(message.getReceiver().getId(), message.getSender().getId())).isEmpty()
        && prietenieDBRepository.findOne((new Tuple<>(message.getSender().getId(), message.getReceiver().getId()))).isEmpty()) {
            throw new ValidationException("Nu sunteti prieteni, deci nu ii poti trimite mesaje!");
        }

        List<Message> all = (List<Message>) getAll();
        long id;
        if (all.isEmpty()) {
            id = 1;
        } else {
            int lastIndex = all.size() - 1;
            id = all.get(lastIndex).getId() + 1;
        }

        message.setId(id);
        if(message.getIdReply()==0)
            message.setIdReply(null);
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
                message.setSender(userDBRepository.findOne(id1).orElseThrow());
                message.setReceiver(userDBRepository.findOne(id2).orElseThrow());
                conversationMessages.add(message);
            }
        }

        return conversationMessages;
    }

    private boolean isPartOfConversation(Message message, Long id1, Long id2) {
        return (message.getSender().getId().equals(id1) && message.getReceiver().getId().equals(id2)) ||
                (message.getSender().getId().equals(id2) && message.getReceiver().getId().equals(id1));
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
