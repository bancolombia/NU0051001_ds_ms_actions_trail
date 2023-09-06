package co.com.bancolombia.model.actions;

import java.util.HashMap;

/**
 * Action represents the data that users have generated in the digital banking in different moments
 * such as monetary and non-monetary transactions.
 * As the structure of the data that is transmitted is not known beforehand, a map is used to transmit the data.
 */
public class Action extends HashMap<String, Object> {
}
