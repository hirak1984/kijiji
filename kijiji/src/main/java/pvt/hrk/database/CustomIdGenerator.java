package pvt.hrk.database;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import pvt.hrk.entity.AdItem;
import pvt.hrk.utils.Utils;

public class CustomIdGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(SessionImplementor arg0, Object arg1) throws HibernateException {
		AdItem item = (AdItem)arg1;
		
		return item.getTitle().hashCode()+Utils.GET_EPOCH_DATE();
	}

}
