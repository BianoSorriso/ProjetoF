package com.scmflusao.events;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * Event bus muito simples para notificar quando transportes são criados/alterados/excluídos,
 * permitindo que o Dashboard atualize imediatamente sem precisar reiniciar a aplicação.
 */
public class DashboardEventBus {
    private static final DashboardEventBus INSTANCE = new DashboardEventBus();

    public static DashboardEventBus getInstance() { return INSTANCE; }

    private final List<Runnable> transporteChangeListeners = new ArrayList<>();

    public synchronized void addTransporteChangeListener(Runnable r) {
        if (r != null) transporteChangeListeners.add(r);
    }

    public synchronized void removeTransporteChangeListener(Runnable r) {
        transporteChangeListeners.remove(r);
    }

    /** Notifica todos ouvintes; garante execução no EDT. */
    public void notifyTransporteChanged() {
        List<Runnable> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(transporteChangeListeners);
        }
        SwingUtilities.invokeLater(() -> {
            for (Runnable r : snapshot) {
                try { r.run(); } catch (Exception ignored) {}
            }
        });
    }
}