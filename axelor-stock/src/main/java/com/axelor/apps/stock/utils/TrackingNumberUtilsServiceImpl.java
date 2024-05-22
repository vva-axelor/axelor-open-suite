/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.stock.utils;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.repo.TraceBackRepository;
import com.axelor.apps.stock.db.TrackingNumber;
import com.axelor.apps.stock.exception.StockExceptionMessage;
import com.axelor.i18n.I18n;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TrackingNumberUtilsServiceImpl implements TrackingNumberUtilsService {

  protected static final int MAX_ITERATION = 1000;

  @Override
  public Set<TrackingNumber> getOriginParents(TrackingNumber trackingNumber)
      throws AxelorException {
    Objects.requireNonNull(trackingNumber);

    if (trackingNumber.getParentTrackingNumberSet() != null
        && !trackingNumber.getParentTrackingNumberSet().isEmpty()) {
      return getOriginParentsRecursive(trackingNumber, 0);
    }
    return Set.of();
  }

  protected Set<TrackingNumber> getOriginParentsRecursive(
      TrackingNumber trackingNumber, int loopNbr) throws AxelorException {

    if (loopNbr >= MAX_ITERATION) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(
              StockExceptionMessage.STOCK_MOVE_TRACKING_NUMBER_PARENT_MAXIMUM_ITERATION_REACHED));
    }

    if (trackingNumber.getParentTrackingNumberSet() != null
        && !trackingNumber.getParentTrackingNumberSet().isEmpty()) {
      HashSet<TrackingNumber> trackingNumbers = new HashSet<>();
      for (TrackingNumber parentTrackingNumber : trackingNumber.getParentTrackingNumberSet()) {
        trackingNumbers.addAll(this.getOriginParentsRecursive(parentTrackingNumber, loopNbr + 1));
      }
      return trackingNumbers;
    }

    return Set.of(trackingNumber);
  }
}
