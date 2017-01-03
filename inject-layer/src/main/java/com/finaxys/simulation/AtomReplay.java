package com.finaxys.simulation;

import configuration.AtomSimulationConfiguration;
import model.atomlogs.AtomLog;
import model.atomlogs.AtomLogFactory;
import model.atomlogs.TimestampedAtomLog;
import model.atomlogs.day.DayLog;
import org.apache.logging.log4j.*;
import v13.*;

import java.util.Collections;

/**
 * @Author raphael on 02/01/2017.
 *
 * Class used to replay a previous ATOM simulation from existing logs.
 * DO NOT WORK when the simulation have pre-opening and closing ticks.
 * FIXME : Voir avec ATOM pourquoi
 */
public class AtomReplay extends Replay {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(AtomReplay.class);
    private AtomSimulationConfiguration atomConf;


    public AtomReplay(String sourceFilename, AtomSimulationConfiguration atomConf) {
        super(sourceFilename);
        this.atomConf = atomConf;
    }


    @Override
    public void handleOneLine(String line) {

        // Instantiate an AtomLog or a TimestampAtomLog depending on the simulation configuration
        AtomLog atomLog = atomConf.isTimestampEnabled()
                ? new TimestampedAtomLog(line, atomConf.isTimestampHumanReadableEnabled()).getAtomLog()
                : AtomLogFactory.createAtomLog(line);

        // Reproduce super.handleOneLine() with the difference that tick and day logs are
        // taking into account to keep track of time when timestamped logs are used
        String atomLogString = atomLog.toString();
        if(!StringOrderParser.isCommentOrEmpty(atomLogString)) {
            if( atomLog.isDayLog() ) {
                sim.getLogger().day( DayLog.class.cast(atomLog).getNumDay(), Collections.emptyList() );
                sim.market.clear();
            } else if(!StringOrderParser.isInfo(atomLogString) && !atomLog.isTickLog()) {
                if(StringOrderParser.isCommand(atomLogString)) {
                    StringOrderParser.parseAndexecuteCommand(atomLogString, sim);
                } else if(atomLog.isOrderLog()) {
                    Order o = StringOrderParser.parseOrder(atomLogString, sim);
                    sim.market.send(o.sender, o);
                }
            } else if (atomLog.isTickLog()) {
                sim.getLogger().tick(sim.day, Collections.emptyList());
            }
            else {
                // Do nothing at the moment
                LOGGER.debug("Ignoring log : " + line);
            }
        }
    }
}