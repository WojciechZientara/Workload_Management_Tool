package pl.coderslab.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import pl.coderslab.entities.Client;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.ClientRepository;
import pl.coderslab.repositories.UserRepository;

public class ClientConverter implements Converter<String, Client> {
    @Autowired
    ClientRepository clientRepository;

    @Override
    public Client convert(String s) {
        return clientRepository.findOne(Long.parseLong(s));
    }
}