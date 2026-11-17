import java.util.ArrayList;
import java.util.List;

public class ObserverAfter {

    private static int failedChecks = 0;

    public static void main(String[] args) {
        EnergyDispatchCenter dispatchCenter = new EnergyDispatchCenter("ПС-110 кВ «Северная»");

        DispatchMimicBoard mimicBoard = new DispatchMimicBoard();
        EmergencyJournal emergencyJournal = new EmergencyJournal();
        RepairCrewAlert repairCrewAlert = new RepairCrewAlert();

        dispatchCenter.addListener(mimicBoard);
        dispatchCenter.addListener(emergencyJournal);
        dispatchCenter.addListener(repairCrewAlert);

        System.out.println("--- Событие 1: авария на линии Л-104 (порядок: Мнемосхема -> Журнал -> Вызов бригады) ---");
        dispatchCenter.reportOutage("КЗ на фидере Л-104");

        check("после первого события оперативный журнал получил ровно 1 запись",
                emergencyJournal.getRecords().size() == 1);
        check("все подписчики получили одно и то же событие (Л-104)",
                mimicBoard.getLastAlert().contains("Л-104")
                && emergencyJournal.getRecords().get(0).contains("Л-104")
                && repairCrewAlert.getDispatchedCount() == 1);

        System.out.println("\n--- Проверка отписки: отключаем вызов аварийной бригады ---");
        dispatchCenter.removeListener(repairCrewAlert);
        dispatchCenter.reportOutage("Отключение трансформатора Т-2");

        check("после отписки службы вызова бригад её счётчик вызовов не увеличился",
                repairCrewAlert.getDispatchedCount() == 1);
        check("оставшиеся подписчики получили второе событие (трансформатор Т-2)",
                emergencyJournal.getRecords().size() == 2
                && mimicBoard.getLastAlert().contains("Т-2"));

        System.out.println("\n--- Тест открытости/закрытости: добавление четвёртого наблюдателя (Телеметрия АСДУ) ---");
        ScadaTelemetry scadaTelemetry = new ScadaTelemetry();
        dispatchCenter.addListener(scadaTelemetry);
        dispatchCenter.reportOutage("Падение напряжения на шинах 10 кВ");

        check("четвёртый наблюдатель получил событие без изменения класса EnergyDispatchCenter",
                scadaTelemetry.getSentPackets().size() == 1
                && scadaTelemetry.getSentPackets().get(0).contains("ПС-110 кВ «Северная»"));

        if (failedChecks > 0) {
            throw new AssertionError("не пройдено проверок: " + failedChecks);
        }
        System.out.println("\nВсе проверки пройдены.");
    }

    static void check(String checkName, boolean passed) {
        System.out.println((passed ? "[OK]   " : "[FAIL] ") + checkName);
        if (!passed) {
            failedChecks++;
        }
    }
}


interface OutageListener {
    void onSubstationOutage(String substationName, String faultDescription);
}

class EnergyDispatchCenter {
    private final String substationName;
    private final List<OutageListener> listeners = new ArrayList<>();

    EnergyDispatchCenter(String substationName) {
        this.substationName = substationName;
    }

    void addListener(OutageListener listener) {
        listeners.add(listener);
    }

    void removeListener(OutageListener listener) {
        listeners.remove(listener);
    }

    void reportOutage(String faultDescription) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onSubstationOutage(substationName, faultDescription);
        }
    }
}


class DispatchMimicBoard implements OutageListener {
    private String lastAlert = "";

    public void onSubstationOutage(String substationName, String faultDescription) {
        lastAlert = "[мнемосхема] ТРЕВОГА на " + substationName + ": " + faultDescription;
        System.out.println(lastAlert);
    }

    String getLastAlert() {
        return lastAlert;
    }
}

class EmergencyJournal implements OutageListener {
    private final List<String> records = new ArrayList<>();

    public void onSubstationOutage(String substationName, String faultDescription) {
        String entry = substationName + " — зафиксирована авария: " + faultDescription;
        records.add(entry);
        System.out.println("[журнал]     " + entry);
    }

    List<String> getRecords() {
        return records;
    }
}

class RepairCrewAlert implements OutageListener {
    private int dispatchedCount = 0;

    public void onSubstationOutage(String substationName, String faultDescription) {
        dispatchedCount++;
        System.out.println("[бригада]    Отправлен наряд ОВБ на " + substationName + " (" + faultDescription + ")");
    }

    int getDispatchedCount() {
        return dispatchedCount;
    }
}

class ScadaTelemetry implements OutageListener {
    private final List<String> sentPackets = new ArrayList<>();

    public void onSubstationOutage(String substationName, String faultDescription) {
        String packet = substationName + "|" + faultDescription;
        sentPackets.add(packet);
        System.out.println("[АСДУ]       Аварийный телесигнал передан в верхний уровень: " + packet);
    }

    List<String> getSentPackets() {
        return sentPackets;
    }
}
