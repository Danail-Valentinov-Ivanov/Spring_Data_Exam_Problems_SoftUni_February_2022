package com.example.football.service.impl;

import com.example.football.models.dto.ImportPlayerDTO;
import com.example.football.models.dto.ImportPlayerRootDTO;
import com.example.football.models.entity.Player;
import com.example.football.models.entity.Stat;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.PlayerRepository;
import com.example.football.repository.StatRepository;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.PlayerService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//ToDo - Implement all methods
@Service
public class PlayerServiceImpl implements PlayerService {
    private final String READ_PLAYER_PATH = "src/main/resources/files/xml/players.xml";
    private final PlayerRepository playerRepository;
    private final TownRepository townRepository;
    private final TeamRepository teamRepository;
    private final StatRepository statRepository;
//    private final TypeMap<ImportPlayerDTO, Player> dtoToPlayerMapper;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, TownRepository townRepository
            , TeamRepository teamRepository, StatRepository statRepository) throws JAXBException {
        this.playerRepository = playerRepository;
        this.townRepository = townRepository;
        this.teamRepository = teamRepository;
        this.statRepository = statRepository;

        JAXBContext jaxbContext = JAXBContext.newInstance(ImportPlayerRootDTO.class);
        unmarshaller = jaxbContext.createUnmarshaller();

        validator = Validation.buildDefaultValidatorFactory().getValidator();
        modelMapper = new ModelMapper();

//        from String to LocalDate
        modelMapper.addConverter(mappingContext -> LocalDate.parse(mappingContext.getSource()
                , DateTimeFormatter.ofPattern("dd/MM/yyyy")), String.class, LocalDate.class);

//        Converting with Converter and TypeMap - DOES NOT WORK!
//        Converter<String, LocalDate>toLocalDate = s -> s.getSource() == null? null :
//                LocalDate.parse(s.getSource(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
////        modelMapper.addConverter(toLocalDate);
//        TypeMap<ImportPlayerDTO, Player>typeMap = modelMapper
//                .createTypeMap(ImportPlayerDTO.class, Player.class);
//        dtoToPlayerMapper = typeMap.addMappings(mapper -> mapper
//                .using(toLocalDate).map(ImportPlayerDTO::getBirthDate, Player::setBirthDate));

        //      This works
//        this.modelMapper.addConverter(new Converter<String, LocalDate>() {
//            @Override
//            public LocalDate convert(MappingContext<String, LocalDate> context) {
//                return LocalDate.parse(context.getSource(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//            }
//        });

//      This also works
//        this.modelMapper.addConverter((Converter<String, LocalDate>)
//                context1 -> LocalDate.parse(context1.getSource(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    @Override
    public boolean areImported() {
        return playerRepository.count() > 0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        Path path = Path.of(READ_PLAYER_PATH);
        return Files.readString(path);
    }

    @Override
    public String importPlayers() throws IOException, JAXBException {
        BufferedReader bufferedReader = Files.newBufferedReader(Path.of(READ_PLAYER_PATH));
        ImportPlayerRootDTO importPlayerRootDTO = (ImportPlayerRootDTO) unmarshaller
                .unmarshal(bufferedReader);
        return importPlayerRootDTO.getImportPlayerDTOS().stream()
                .map(importPlayerDTO -> importPlayer(importPlayerDTO))
                .collect(Collectors.joining("\n"));
    }

    private String importPlayer(ImportPlayerDTO importPlayerDTO) {
        Set<ConstraintViolation<ImportPlayerDTO>> validationErrors = validator.validate(importPlayerDTO);
        if (!validationErrors.isEmpty()) {
            return "Invalid Player";
        }
        Optional<Player> optionalPlayer = playerRepository.findByEmail(importPlayerDTO.getEmail());
        if (optionalPlayer.isPresent()) {
            return "Invalid Player";
        }
        Optional<Town> town = townRepository.findByName(importPlayerDTO.getTown().getName());
        Optional<Team> team = teamRepository.findByName(importPlayerDTO.getTeam().getName());
        Optional<Stat> stat = statRepository.findById(importPlayerDTO.getStat().getId());

        Player player = modelMapper.map(importPlayerDTO, Player.class);
        player.setTown(town.get());
        player.setTeam(team.get());
        player.setStat(stat.get());

        playerRepository.save(player);
        return "Successfully imported Player " + player.getFirstName() + " " +
                player.getLastName() + " - " + player.getPosition().toString();
    }

    @Override
    public String exportBestPlayers() {
//    after 01-01-1995 and before 01-01-2003
        LocalDate after = LocalDate.of(1995, 1, 2);
        LocalDate before = LocalDate.of(2002, 12, 31);

        List<Player> players = playerRepository
                .findByBirthDateBetweenOrderByStatShootingDescStatPassingDescStatEnduranceDescLastNameAsc(after
                        , before);
        return players.stream().map(player -> player.toString()).collect(Collectors.joining("\n"));
    }
}
