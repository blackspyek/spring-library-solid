package org.pollub.branch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.branch.model.LibraryBranch;
import org.pollub.branch.repository.BranchRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final BranchRepository branchRepository;

    @Override
    public void run(String... args) {
        if (branchRepository.count() == 0) {
            log.info("No branches found. Populating database with legacy branch data...");

            List<LibraryBranch> branches = Arrays.asList(
                createBranch("Filia nr 1", "1", "ul. Kościelna 7a", "Lublin", "51.2465", "22.5684", "Poniedziałek: 09:00-18:00|Wtorek: 09:00-18:00|Środa: 09:00-18:00|Czwartek: 09:00-18:00|Piątek: 09:00-18:00"),
                createBranch("Filia nr 6 BIOTEKA MEDIATEKA", "6", "ul. Aleje Racławickie 22", "Lublin", "51.2463", "22.5312", "Poniedziałek: 10:00-19:00|Wtorek: 10:00-19:00|Środa: 10:00-19:00|Czwartek: 10:00-19:00|Piątek: 10:00-19:00"),
                createBranch("Filia nr 12", "12", "ul. Żelazowej Woli 7", "Lublin", "51.2298", "22.4891", "Poniedziałek: 08:00-16:00|Wtorek: 08:00-16:00|Środa: 08:00-16:00|Czwartek: 08:00-16:00|Piątek: 08:00-16:00"),
                createBranch("Filia nr 18", "18", "ul. Głęboka 8a", "Lublin", "51.2507", "22.5523", "Poniedziałek: 09:00-17:00|Wtorek: 09:00-17:00|Środa: 09:00-17:00|Czwartek: 09:00-17:00|Piątek: 09:00-17:00"),
                createBranch("Filia nr 21", "21", "ul. Rynek 11", "Lublin", "51.2475", "22.5657", "Poniedziałek: 10:00-18:00|Wtorek: 10:00-18:00|Środa: 10:00-18:00|Czwartek: 10:00-18:00|Piątek: 10:00-18:00"),
                createBranch("Filia nr 29", "29", "ul. Kiepury 5", "Lublin", "51.2351", "22.4879", "Poniedziałek: 08:30-16:30|Wtorek: 08:30-16:30|Środa: 08:30-16:30|Czwartek: 08:30-16:30|Piątek: 08:30-16:30"),
                createBranch("Filia nr 30", "30", "ul. Braci Wieniawskich 5", "Lublin", "51.2375", "22.5002", "Poniedziałek: 10:00-20:00|Wtorek: 10:00-20:00|Środa: 10:00-20:00|Czwartek: 10:00-20:00|Piątek: 10:00-20:00"),
                createBranch("Filia nr 31", "31", "ul. Nałkowskich 104", "Lublin", "51.2276", "22.4934", "Poniedziałek: 09:00-17:00|Wtorek: 09:00-17:00|Środa: 09:00-17:00|Czwartek: 09:00-17:00|Piątek: 09:00-17:00"),
                createBranch("Filia nr 32 BIBLIO MEDIATEKA", "32", "ul. Szaserów 13-15", "Lublin", "51.2642", "22.5189", "Poniedziałek: 09:00-19:00|Wtorek: 09:00-19:00|Środa: 09:00-19:00|Czwartek: 09:00-19:00|Piątek: 09:00-19:00"),
                createBranch("Filia nr 40 BIBLIOTEKA NA POZIOMIE", "40", "ul. Sławin 20", "Lublin", "51.2712", "22.5098", "Poniedziałek: 10:00-18:00|Wtorek: 10:00-18:00|Środa: 10:00-18:00|Czwartek: 10:00-18:00|Piątek: 10:00-18:00")
            );

            branchRepository.saveAll(branches);
            
            log.info("Successfully populated {} branches from legacy data.", branches.size());
        }
    }

    private LibraryBranch createBranch(String name, String number, String address, String city, String lat, String lon, String hours) {
        LibraryBranch branch = new LibraryBranch();
        branch.setName(name);
        branch.setBranchNumber(number);
        branch.setAddress(address);
        branch.setCity(city);
        branch.setLatitude(Double.parseDouble(lat));
        branch.setLongitude(Double.parseDouble(lon));
        branch.setOpeningHours(hours);
        
        // Fake contact data
        branch.setPhone("81 745 " + String.format("%02d", Integer.parseInt(number)) + " 00");
        branch.setEmail("filia" + number + "@mbp.lublin.pl");
        
        return branch;
    }
}
