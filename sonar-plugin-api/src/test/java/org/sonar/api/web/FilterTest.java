/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.web;

import org.junit.rules.ExpectedException;

import org.junit.Rule;

import org.junit.Test;

public class FilterTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void should_accept_valid_periods() {
    Filter.create().setDefaultPeriod("list");
    Filter.create().setDefaultPeriod("treemap");
  }

  @Test
  public void should_fail_on_invalid_period() {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("Default period should be either list or treemap, not <invalid>");

    Filter.create().setDefaultPeriod("<invalid>");
  }
}
