package org.tno.wpclient;

import javax.swing.table.AbstractTableModel;

import org.pathvisio.wikipathways.webservice.WSSearchResult;

	/**
	 * This class creates the ResultTableModel 
	 * Based on the results
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