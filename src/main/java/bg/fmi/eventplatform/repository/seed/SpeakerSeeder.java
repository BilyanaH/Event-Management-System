package bg.fmi.eventplatform.repository.seed;

import bg.fmi.eventplatform.domain.Speaker;
import bg.fmi.eventplatform.repository.SpeakerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(3)
@Component
@Profile("!test")
public class SpeakerSeeder implements CommandLineRunner {

    private final SpeakerRepository speakerRepository;

    public SpeakerSeeder(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

    @Override
    public void run(String... args) {
        if (speakerRepository.count() > 0) {
            return;
        }

        Speaker jane = new Speaker();
        jane.setName("Jane Petrova");
        jane.setBio("Distinguished engineer with 15+ years working on distributed systems.");
        jane.setCompany("CloudWorks");
        jane.setTitlePosition("Principal Engineer");

        Speaker dimitar = new Speaker();
        dimitar.setName("Dimitar Stoyanov");
        dimitar.setBio("Independent music producer and DJ from Plovdiv.");
        dimitar.setCompany("Freelance");
        dimitar.setTitlePosition("Producer");

        Speaker maria = new Speaker();
        maria.setName("Maria Georgieva");
        maria.setBio("Sommelier specialising in Bulgarian wine regions.");
        maria.setCompany("Vino Bulgaria");
        maria.setTitlePosition("Head Sommelier");

        speakerRepository.save(jane);
        speakerRepository.save(dimitar);
        speakerRepository.save(maria);
    }
}
