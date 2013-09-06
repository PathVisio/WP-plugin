// PathVisio WP Client
// Plugin that provides a WikiPathways client for PathVisio.
// Copyright 2013 developed for Google Summer of Code
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.wpclient;

import javax.swing.table.AbstractTableModel;

import org.pathvisio.wikipathways.webservice.WSSearchResult;

/**
 * This class creates the ResultTableModel 
 * Based on the results
 * @author Sravanthi Sinha
 * @author Martina Kutmon
 * @version 1.0 
 */
	 class ResultTableModel extends AbstractTableModel
	{
		WSSearchResult[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species" };
		String clientName;

		public ResultTableModel(WSSearchResult[] results, String clientName) 
		{
			this.clientName = clientName;
			this.results = results;
		}

		public int getColumnCount()
		{
			return 3;
		}

		public int getRowCount() 
		{
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			WSSearchResult r = results[rowIndex];
			switch (columnIndex) {
			case 0:
				return r.getId();
			case 1:
				return r.getName();
			case 2:
				return r.getSpecies();
			}
			return "";
		}

		public String getColumnName(int column) 
		{
			return columnNames[column];
		}
	}