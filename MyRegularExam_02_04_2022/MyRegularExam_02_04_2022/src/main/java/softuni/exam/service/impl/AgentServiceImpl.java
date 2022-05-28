package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportAgentDTO;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.AgentService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgentServiceImpl implements AgentService {
    private final String READ_AGENT_PATH = "src/main/resources/files/json/agents.json";
    private final AgentRepository agentRepository;
    private final TownRepository townRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository, TownRepository townRepository
            , Gson gson, Validator validator, ModelMapper modelMapper) {
        this.agentRepository = agentRepository;
        this.townRepository = townRepository;

        this.gson = gson;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return agentRepository.count() > 0;
    }

    @Override
    public String readAgentsFromFile() throws IOException {
        Path path = Path.of(READ_AGENT_PATH);
        return Files.readString(path);
    }

    @Override
    public String importAgents() throws IOException {
        String jsonString = readAgentsFromFile();
        ImportAgentDTO[] importTownDTOS = gson.fromJson(jsonString, ImportAgentDTO[].class);
        return Arrays.stream(importTownDTOS).map(dto -> importAgent(dto))
                .collect(Collectors.joining("\n"));
    }

    private String importAgent(ImportAgentDTO dto) {
        Set<ConstraintViolation<ImportAgentDTO>> validationErrors = validator.validate(dto);

        if (validationErrors.isEmpty()){
            Optional<Agent> optionalAgent = agentRepository.findByFirstName(dto.getFirstName());
            if (optionalAgent.isEmpty()){
                Agent agent = modelMapper.map(dto, Agent.class);

                Optional<Town>town = townRepository.findByTownName(dto.getTown());
                agent.setTown(town.get());
                agentRepository.save(agent);
                return "Successfully imported agent " + agent.toString();
            } else {
                return "Invalid agent";
            }
        }
        return "Invalid agent";
    }
}
