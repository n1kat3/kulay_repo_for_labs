import java.util.LinkedHashMap;
import java.util.Map;

public class FactoryAfter {

    private static int failedChecks = 0;

    public static void main(String[] args) {
        Map<String, ConsumerCreator> consumerCreators = new LinkedHashMap<>();
        consumerCreators.put("INDUSTRIAL", new IndustrialConsumerCreator());
        consumerCreators.put("RESIDENTIAL", new ResidentialConsumerCreator());
        consumerCreators.put("MUNICIPAL", new MunicipalConsumerCreator());

        System.out.println("--- Создание базовых типов потребителей электроэнергии ---");
        PowerConsumer industrial = consumerCreators.get("INDUSTRIAL").createConsumer(1500.0);
        PowerConsumer residential = consumerCreators.get("RESIDENTIAL").createConsumer(15.0);
        PowerConsumer municipal = consumerCreators.get("MUNICIPAL").createConsumer(250.0);

        System.out.println(industrial.description());
        System.out.println(residential.description());
        System.out.println(municipal.description());

        System.out.println("\n--- Характеризующие и структурные проверки ---");
        check("описание промышленного потребителя не пустое и содержит мощность",
                !industrial.description().isEmpty() && industrial.description().contains("1500.0"));
        check("описание бытового потребителя не пустое и содержит мощность",
                !residential.description().isEmpty() && residential.description().contains("15.0"));
        check("описание бюджетного потребителя не пустое и содержит мощность",
                !municipal.description().isEmpty() && municipal.description().contains("250.0"));

        check("создатель промышленных потребителей возвращает IndustrialConsumer",
                industrial instanceof IndustrialConsumer);
        check("создатель бытовых потребителей возвращает ResidentialConsumer",
                residential instanceof ResidentialConsumer);
        check("создатель бюджетных потребителей возвращает MunicipalConsumer",
                municipal instanceof MunicipalConsumer);

        System.out.println("\n--- Тест открытости/закрытости: добавление четвёртого типа (AgriculturalConsumer) ---");
        ConsumerCreator agriculturalCreator = new AgriculturalConsumerCreator();
        PowerConsumer agricultural = agriculturalCreator.createConsumer(420.0);
        System.out.println(agricultural.description());

        check("сельскохозяйственный потребитель создан новым создателем и работает корректно",
                agricultural instanceof AgriculturalConsumer
                && agricultural.allocatedPowerKw() == 420.0
                && agricultural.description().contains("420.0"));

        verifyAllPassed();
        System.out.println("\nВсе проверки пройдены.");
    }

    static void check(String checkName, boolean passed) {
        System.out.println((passed ? "[OK]   " : "[FAIL] ") + checkName);
        if (!passed) {
            failedChecks++;
        }
    }

    static void verifyAllPassed() {
        if (failedChecks > 0) {
            throw new AssertionError("не пройдено проверок: " + failedChecks);
        }
    }
}

interface PowerConsumer {
    String description();
    double allocatedPowerKw();
}

abstract class ConsumerCreator {
    public abstract PowerConsumer createConsumer(double allocatedPowerKw);
}


class IndustrialConsumer implements PowerConsumer {
    private final double allocatedPowerKw;

    IndustrialConsumer(double allocatedPowerKw) {
        this.allocatedPowerKw = allocatedPowerKw;
    }

    public double allocatedPowerKw() {
        return allocatedPowerKw;
    }

    public String description() {
        return "Промышленный потребитель: выделенная мощность " + allocatedPowerKw + " кВт, 1-я категория надёжности";
    }
}

class ResidentialConsumer implements PowerConsumer {
    private final double allocatedPowerKw;

    ResidentialConsumer(double allocatedPowerKw) {
        this.allocatedPowerKw = allocatedPowerKw;
    }

    public double allocatedPowerKw() {
        return allocatedPowerKw;
    }

    public String description() {
        return "Бытовой потребитель: выделенная мощность " + allocatedPowerKw + " кВт, 3-я категория надёжности";
    }
}

class MunicipalConsumer implements PowerConsumer {
    private final double allocatedPowerKw;

    MunicipalConsumer(double allocatedPowerKw) {
        this.allocatedPowerKw = allocatedPowerKw;
    }

    public double allocatedPowerKw() {
        return allocatedPowerKw;
    }

    public String description() {
        return "Бюджетное учреждение: выделенная мощность " + allocatedPowerKw + " кВт, 2-я категория надёжности";
    }
}

class AgriculturalConsumer implements PowerConsumer {
    private final double allocatedPowerKw;

    AgriculturalConsumer(double allocatedPowerKw) {
        this.allocatedPowerKw = allocatedPowerKw;
    }

    public double allocatedPowerKw() {
        return allocatedPowerKw;
    }

    public String description() {
        return "Сельскохозяйственный потребитель: выделенная мощность " + allocatedPowerKw + " кВт, сезонная нагрузка";
    }
}


class IndustrialConsumerCreator extends ConsumerCreator {
    public PowerConsumer createConsumer(double allocatedPowerKw) {
        return new IndustrialConsumer(allocatedPowerKw);
    }
}

class ResidentialConsumerCreator extends ConsumerCreator {
    public PowerConsumer createConsumer(double allocatedPowerKw) {
        return new ResidentialConsumer(allocatedPowerKw);
    }
}

class MunicipalConsumerCreator extends ConsumerCreator {
    public PowerConsumer createConsumer(double allocatedPowerKw) {
        return new MunicipalConsumer(allocatedPowerKw);
    }
}

/** Создатель для четвёртого типа потребителя. */
class AgriculturalConsumerCreator extends ConsumerCreator {
    public PowerConsumer createConsumer(double allocatedPowerKw) {
        return new AgriculturalConsumer(allocatedPowerKw);
    }
}
