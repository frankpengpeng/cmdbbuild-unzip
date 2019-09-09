package org.cmdbuild.dao.entrytype.attributetype;

import static com.google.common.base.Preconditions.checkArgument;
import java.math.BigDecimal;

import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public class DecimalAttributeType implements CardAttributeType<BigDecimal> {

	private final Integer precision;
	private final Integer scale;

	public DecimalAttributeType() {
		this.precision = null;
		this.scale = null;
	}

	public DecimalAttributeType(@Nullable Integer precision, @Nullable Integer scale) {
		if (precision == null && scale == null) {
			this.precision = this.scale = null;
		} else {
			checkNotNullAndGtZero(precision, "invalid decimal attr precision = %s", precision);
			checkNotNullAndGtZero(scale, "invalid decimal attr scale = %s", scale);
			checkArgument(precision >= scale, "invalid decimal attr params: precision = %s scale = %s (precision must be >= scale)", precision, scale);
			this.precision = precision;
			this.scale = scale;
		}
	}

	@Override
	public void accept(final CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.DECIMAL;
	}

	@Nullable
	public Integer getPrecision() {
		return precision;
	}

	@Nullable
	public Integer getScale() {
		return scale;
	}

	public boolean hasPrecisionAndScale() {
		return precision != null && scale != null;
	}

}
